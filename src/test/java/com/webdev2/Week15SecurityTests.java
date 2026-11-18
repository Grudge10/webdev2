package com.webdev2;

import com.webdev2.model.ClinicAccount;
import com.webdev2.repository.ClinicAccountRepository;
import com.webdev2.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class Week15SecurityTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ClinicAccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PetRepository petRepository;

    private String extractJsonField(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    @Test
    void fullSessionCsrfAndRoleAuthorizationLifecycle() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        ClinicAccount reader = accountRepository.save(
                new ClinicAccount("test_reader_w15", passwordEncoder.encode("ReaderPass_W15!"), "READER", true));
        ClinicAccount manager = accountRepository.save(
                new ClinicAccount("test_manager_w15", passwordEncoder.encode("ManagerPass_W15!"), "MANAGER", true));

        try {
            // 1. Anonymous protected read is rejected with 401 JSON
            mockMvc.perform(get("/api/pets"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401));

            // Fetch pre-login CSRF token and session
            MockHttpSession readerSession = new MockHttpSession();
            MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf").session(readerSession))
                    .andExpect(status().isOk())
                    .andReturn();
            String readerCsrf = extractJsonField(csrfResult.getResponse().getContentAsString(), "token");
            assertNotNull(readerCsrf);

            // 2. Invalid password is denied with 401
            mockMvc.perform(post("/api/auth/login")
                            .session(readerSession)
                            .header("X-CSRF-TOKEN", readerCsrf)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"test_reader_w15\",\"password\":\"WrongPass\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401));

            // 3. Valid READER login succeeds without exposing password hash
            MvcResult loginRes = mockMvc.perform(post("/api/auth/login")
                            .session(readerSession)
                            .header("X-CSRF-TOKEN", readerCsrf)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"test_reader_w15\",\"password\":\"ReaderPass_W15!\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("test_reader_w15"))
                    .andExpect(jsonPath("$.role").value("READER"))
                    .andReturn();
            assertFalse(loginRes.getResponse().getContentAsString().contains("passwordHash"));

            // Refresh CSRF token after login rotation
            MvcResult postLoginCsrfRes = mockMvc.perform(get("/api/auth/csrf").session(readerSession))
                    .andExpect(status().isOk())
                    .andReturn();
            String validReaderCsrf = extractJsonField(postLoginCsrfRes.getResponse().getContentAsString(), "token");

            // 4. Permitted READER role reads pets
            mockMvc.perform(get("/api/pets").session(readerSession))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            // 5. READER cannot mutate even with valid CSRF token (403 role denial, rows unchanged)
            long beforePetCount = petRepository.count();
            mockMvc.perform(post("/api/pets")
                            .session(readerSession)
                            .header("X-CSRF-TOKEN", validReaderCsrf)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"microchipNumber\":\"MC-READER-BLOCK\",\"name\":\"NoWrite\",\"species\":\"Dog\"}"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value(403));
            assertEquals(beforePetCount, petRepository.count());

            // 6. MANAGER login + missing CSRF check + authorized create/delete + logout
            MockHttpSession mgrSession = new MockHttpSession();
            MvcResult mgrPreCsrfRes = mockMvc.perform(get("/api/auth/csrf").session(mgrSession))
                    .andExpect(status().isOk())
                    .andReturn();
            String mgrPreCsrf = extractJsonField(mgrPreCsrfRes.getResponse().getContentAsString(), "token");

            mockMvc.perform(post("/api/auth/login")
                            .session(mgrSession)
                            .header("X-CSRF-TOKEN", mgrPreCsrf)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"test_manager_w15\",\"password\":\"ManagerPass_W15!\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("MANAGER"));

            // Missing CSRF token fails with 403 even when authenticated as MANAGER
            mockMvc.perform(post("/api/pets")
                            .session(mgrSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"microchipNumber\":\"MC-NOCSRF\",\"name\":\"Blocked\",\"species\":\"Dog\"}"))
                    .andExpect(status().isForbidden());

            MvcResult mgrCsrfRes = mockMvc.perform(get("/api/auth/csrf").session(mgrSession))
                    .andExpect(status().isOk())
                    .andReturn();
            String mgrCsrf = extractJsonField(mgrCsrfRes.getResponse().getContentAsString(), "token");

            // 7. Authorized MANAGER with valid CSRF creates and deletes a pet
            MvcResult createdRes = mockMvc.perform(post("/api/pets")
                            .session(mgrSession)
                            .header("X-CSRF-TOKEN", mgrCsrf)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"microchipNumber\":\"MC-MGR-8001\",\"name\":\"SecTestPet\",\"species\":\"Cat\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();
            String createdId = createdRes.getResponse().getHeader("Location");
            String idPart = createdId.substring(createdId.lastIndexOf('/') + 1);

            mockMvc.perform(delete("/api/pets/" + idPart)
                            .session(mgrSession)
                            .header("X-CSRF-TOKEN", mgrCsrf))
                    .andExpect(status().isNoContent());

            // 8. Logout invalidates session so protected reads fail with 401 again
            mockMvc.perform(post("/api/auth/logout")
                            .session(mgrSession)
                            .header("X-CSRF-TOKEN", mgrCsrf))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/pets").session(mgrSession))
                    .andExpect(status().isUnauthorized());
        } finally {
            accountRepository.deleteById(reader.getId());
            accountRepository.deleteById(manager.getId());
        }
    }
}
