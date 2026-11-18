package com.webdev2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class Week16ApiSecurityTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void rejectsAnonymousProtectedReadWithUnauthorized() throws Exception {
        mockMvc().perform(get("/api/pets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"READER"})
    void rejectsReaderMutationEvenWithValidCsrf() throws Exception {
        mockMvc().perform(post("/api/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"microchipNumber\":\"MC-F4-BLK\",\"name\":\"NoAccess\",\"species\":\"Dog\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void rejectsInvalidPetPayloadWithValidationErrors() throws Exception {
        mockMvc().perform(post("/api/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"microchipNumber\":\"MC-F4-BAD\",\"name\":\"A\",\"species\":\"Dog\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void rejectsDuplicateMicrochipAndReturnsNotFoundForMissingPet() throws Exception {
        mockMvc().perform(post("/api/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"microchipNumber\":\"MC-1001\",\"name\":\"DupPet\",\"species\":\"Dog\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mockMvc().perform(get("/api/pets/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void rejectsInvalidSortParameterWithBadRequest() throws Exception {
        mockMvc().perform(get("/api/pets?sort=dropTable"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void allowsManagerToCreateUpdateAndDeletePet() throws Exception {
        MvcResult created = mockMvc().perform(post("/api/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"microchipNumber\":\"MC-F4-OK\",\"name\":\"Nala\",\"species\":\"Cat\",\"breed\":\"Tabby\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc().perform(put("/api/pets/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"microchipNumber\":\"MC-F4-OK\",\"name\":\"Nala Updated\",\"species\":\"Cat\",\"breed\":\"Calico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nala Updated"));

        mockMvc().perform(delete("/api/pets/" + id).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
