package com.lcwd.electronic.store.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Electronic Store Backend APIs")
                        .description("API documentation for Electronic Store")
                        .version("1.0")
                        .termsOfService("https://www.google.com")
                        .contact(new Contact()
                                .name("Abhishek Kumar")
                                .email("abhishekkumarr0419@gmail.com")
                                .url("https://instagram.com"))
                        .license(new License()
                                .name("License of APIs")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                )
//1. components()        → Define HOW authentication works
//2. addSecurityItem()   → Tell APIs to USE that authentication
                // Define JWT security scheme
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )

                // Apply JWT security to APIs
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }

}

