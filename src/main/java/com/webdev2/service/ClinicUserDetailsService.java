package com.webdev2.service;

import com.webdev2.model.ClinicAccount;
import com.webdev2.repository.ClinicAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClinicUserDetailsService implements UserDetailsService {
    private final ClinicAccountRepository accountRepository;

    public ClinicUserDetailsService(ClinicAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClinicAccount account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + username));

        return new User(
                account.getUsername(),
                account.getPasswordHash(),
                account.isEnabled(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole()))
        );
    }
}
