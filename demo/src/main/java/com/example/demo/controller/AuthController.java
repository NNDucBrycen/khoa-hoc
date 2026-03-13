package com.example.demo.controller;

import com.example.demo.dto.CsrfResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.LogoutResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Session-based login, CSRF token, and logout")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final CsrfTokenRepository csrfTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          CsrfTokenRepository csrfTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    // ── POST /api/v1/auth/login (AC-1, AC-2, AC-6, AC-7) ──────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login with username and password",
            description = "Authenticates user and creates a server-side session. Returns session cookie.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Login successful"),
                @ApiResponse(responseCode = "401", description = "Invalid credentials",
                        content = @Content(mediaType = "application/problem+json",
                                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
                @ApiResponse(responseCode = "400", description = "Validation error",
                        content = @Content(mediaType = "application/problem+json"))
            })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication token = UsernamePasswordAuthenticationToken.unauthenticated(
                request.username(), request.password());
        // authenticate() throws AuthenticationException on failure → handled by GlobalExceptionHandler
        Authentication authentication = authenticationManager.authenticate(token);

        // Persist authentication into the HTTP session
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(true, authentication.getName(), roles));
    }

    // ── GET /api/v1/auth/csrf (AC-9, AC-10, AC-11) ────────────────────────────
    @GetMapping("/csrf")
    @Operation(summary = "Get CSRF token",
            description = "Returns the current CSRF token for the authenticated session. " +
                    "Call immediately after login and on app init (if session is still valid). " +
                    "Requires: valid session cookie (JSESSIONID).",
            responses = {
                @ApiResponse(responseCode = "200", description = "CSRF token returned"),
                @ApiResponse(responseCode = "401", description = "No valid session",
                        content = @Content(mediaType = "application/problem+json"))
            })
    public ResponseEntity<CsrfResponse> getCsrf(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken token = csrfTokenRepository.loadToken(request);
        if (token == null) {
            token = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(token, request, response);
        }
        return ResponseEntity.ok(new CsrfResponse(token.getToken(), token.getHeaderName(), token.getParameterName()));
    }

    // ── POST /api/v1/auth/logout (AC-16, AC-17, AC-18) ────────────────────────
    @PostMapping("/logout")
    @Operation(summary = "Logout",
            description = "Invalidates the current session and clears the session cookie. " +
                    "Requires: valid session + X-CSRF-TOKEN header.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Logout successful"),
                @ApiResponse(responseCode = "401", description = "No valid session"),
                @ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
            })
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Clear session cookie (AC-18)
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(new LogoutResponse(true));
    }
}
