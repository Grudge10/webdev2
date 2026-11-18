package com.webdev2.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CsrfTokenRepository csrfTokenRepository
    ) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .securityContext(ctx -> ctx.securityContextRepository(securityContextRepository))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health", "/api/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pets/**", "/api/owners/**", "/api/veterinarians/**", "/api/appointments/**")
                        .hasAnyRole("READER", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/pets/**", "/api/owners/**", "/api/appointments/**")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/pets/**", "/api/owners/**")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/api/pets/**")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/pets/**", "/api/owners/**")
                        .hasRole("MANAGER")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) -> writeError(res, 401, "Authentication required"))
                        .accessDeniedHandler((req, res, deniedEx) -> writeError(res, 403, "Forbidden: insufficient role or invalid CSRF token")));
        return http.build();
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        String safe = message.replace("\\", "\\\\").replace("\"", "\\\"");
        String body = "{\"status\":" + status + ",\"message\":\"" + safe + "\",\"timestamp\":" + System.currentTimeMillis() + ",\"errors\":null}";
        response.getWriter().write(body);
    }
}
