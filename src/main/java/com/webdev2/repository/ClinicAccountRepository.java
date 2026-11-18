package com.webdev2.repository;

import com.webdev2.model.ClinicAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClinicAccountRepository extends JpaRepository<ClinicAccount, Long> {
    Optional<ClinicAccount> findByUsername(String username);
}
