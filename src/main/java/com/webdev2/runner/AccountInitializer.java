package com.webdev2.runner;

import com.webdev2.model.ClinicAccount;
import com.webdev2.repository.ClinicAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountInitializer implements CommandLineRunner {
    private final ClinicAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String readerUsername;
    private final String readerPassword;
    private final String managerUsername;
    private final String managerPassword;

    public AccountInitializer(
            ClinicAccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.reader-username:}") String readerUsername,
            @Value("${app.seed.reader-password:}") String readerPassword,
            @Value("${app.seed.manager-username:}") String managerUsername,
            @Value("${app.seed.manager-password:}") String managerPassword
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.readerUsername = readerUsername;
        this.readerPassword = readerPassword;
        this.managerUsername = managerUsername;
        this.managerPassword = managerPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedIfProvided(readerUsername, readerPassword, "READER");
        seedIfProvided(managerUsername, managerPassword, "MANAGER");
    }

    private void seedIfProvided(String username, String rawPassword, String role) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return;
        }
        String trimmed = username.trim();
        if (accountRepository.findByUsername(trimmed).isEmpty()) {
            accountRepository.save(new ClinicAccount(trimmed, passwordEncoder.encode(rawPassword), role, true));
        }
    }
}
