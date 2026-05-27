package com.smartbookfinder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Book Finder API")
                        .description("API for searching books via OpenLibrary and managing favorites & search history")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Smart Book Finder Team")));
    }
}
