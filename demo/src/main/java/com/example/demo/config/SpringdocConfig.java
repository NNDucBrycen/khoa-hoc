package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configures Springdoc OpenAPI documentation.
 * This bean is only created for dev and staging profiles (AC-20, AC-21, SEC-8).
 * In prod, springdoc.api-docs.enabled=false in application-prod.properties disables endpoints.
 */
@Configuration
@Profile({"dev", "staging", "test"})
public class SpringdocConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo Auth API")
                        .version("1.0")
                        .description("""
                                Session-based authentication API.

                                **Cookie policy**: All environments use HttpOnly=true, SameSite=None.
                                Secure=true is required in staging/prod.

                                **CSRF flow**:
                                1. POST /api/v1/auth/login → receive JSESSIONID cookie
                                2. GET /api/v1/auth/csrf → receive csrfToken
                                3. Attach X-CSRF-TOKEN header on all mutating requests (POST/PUT/PATCH/DELETE)
                                """));
    }
}
