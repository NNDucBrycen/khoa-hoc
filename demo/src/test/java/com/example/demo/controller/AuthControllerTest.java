package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.LoginRequest;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean UserDetailsServiceImpl userDetailsService;

    // ── Login tests ───────────────────────────────────────────────────────────

    @Test
    void login_withValidCredentials_returns200WithSetCookie() throws Exception {
        // UserDetailsService returning valid user triggers Spring Security to authenticate
        given(userDetailsService.loadUserByUsername("user01"))
                .willReturn(User.withUsername("user01")
                        .password("$2b$12$TJqx3Tzdl0ZzRNVW6s7HP.GJOBUhLqtTWlHqZ/yoMWvkT3tqH/oUy")
                        .roles("USER").build());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("user01", "User@123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("user01"))
                .andExpect(jsonPath("$.roles", hasItem("USER")));
    }

    @Test
    void login_withWrongPassword_returns401ProblemDetail() throws Exception {
        given(userDetailsService.loadUserByUsername("user01"))
                .willReturn(User.withUsername("user01")
                        .password("$2b$12$TJqx3Tzdl0ZzRNVW6s7HP.GJOBUhLqtTWlHqZ/yoMWvkT3tqH/oUy")
                        .roles("USER").build());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("user01", "wrongpassword"))))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.type").value(containsString("invalid-credentials")))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists());
    }

    @Test
    void login_withBlankUsername_returns400ProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("", "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ── Unauthenticated access tests ──────────────────────────────────────────

    @Test
    void csrf_withoutSession_returns401ProblemDetail() throws Exception {
        mockMvc.perform(get("/api/v1/auth/csrf"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void protectedEndpoint_withoutSession_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/protected-resource"))
                .andExpect(status().isUnauthorized());
    }
}
