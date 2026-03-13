package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ── BCrypt cost 12 (SEC-3, NFR-1) ──────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // ── Shared session-based security context repository ───────────────────────
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // ── CSRF token repository (raw token, header-based) ───────────────────────
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
        repo.setHeaderName("X-CSRF-TOKEN");
        return repo;
    }

    // ── Authentication manager ─────────────────────────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Main security filter chain ─────────────────────────────────────────────
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                // Login is public — no session/CSRF yet for the caller
                .ignoringRequestMatchers("/api/v1/auth/login")
                .csrfTokenRepository(csrfTokenRepository())
                // CsrfTokenRequestAttributeHandler stores raw token; compatible with header-based CSRF
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .securityContext(sc -> sc.securityContextRepository(securityContextRepository()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(fl -> fl.disable())
            .httpBasic(hb -> hb.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(this::handleUnauthorized)
                .accessDeniedHandler(this::handleForbidden)
            );

        return http.build();
    }

    // ── CORS (SEC-5, AC-30, AC-31) ─────────────────────────────────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));   // explicit whitelist, no wildcard
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ── RFC 7807 handlers (SEC-9, AC-32) ──────────────────────────────────────
    private void handleUnauthorized(HttpServletRequest req, HttpServletResponse res,
                                    org.springframework.security.core.AuthenticationException ex)
            throws java.io.IOException {
        ProblemDetail problem = ProblemDetail.forStatus(401);
        problem.setType(URI.create("https://errors.example.com/auth/session-required"));
        problem.setTitle("Unauthorized");
        problem.setDetail("Authentication is required to access this resource.");
        problem.setInstance(URI.create(req.getRequestURI()));
        writeProblemDetail(res, 401, problem);
    }

    private void handleForbidden(HttpServletRequest req, HttpServletResponse res,
                                 org.springframework.security.access.AccessDeniedException ex)
            throws java.io.IOException {
        ProblemDetail problem = ProblemDetail.forStatus(403);
        problem.setType(URI.create("https://errors.example.com/auth/forbidden"));
        problem.setTitle("Forbidden");
        problem.setDetail("CSRF token missing or invalid, or access is not permitted.");
        problem.setInstance(URI.create(req.getRequestURI()));
        writeProblemDetail(res, 403, problem);
    }

    private void writeProblemDetail(HttpServletResponse res, int status, ProblemDetail problem)
            throws java.io.IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(res.getWriter(), problem);
    }
}
