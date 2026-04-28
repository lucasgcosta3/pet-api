package com.lucascosta.petapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pet API",
                version = "v1",
                description = "API for pet management"
        )
)
public class OpenApiConfig {
}