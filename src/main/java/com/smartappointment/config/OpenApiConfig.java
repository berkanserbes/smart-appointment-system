package com.smartappointment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * 
 * This configuration adds JWT Bearer token authentication support to Swagger
 * UI,
 * enabling the "Authorize" button for testing api endpoints.
 * 
 * Usage:
 * 1. Click the "Authorize" button in the top-right corner of Swagger UI
 * 2. Enter your JWT token (without "Bearer" prefix - it's added automatically)
 * 3. All subsequent requests will include the Authorization header
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Smart Appointment System API")
                        .description("RESTful API for Smart Appointment and Reminder System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("berkan_serbes@hotmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                        "Enter your JWT token. The 'Bearer' prefix will be added automatically.")));
    }
}
