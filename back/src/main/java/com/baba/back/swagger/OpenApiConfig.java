package com.baba.back.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_KEY = "apiKey";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(API_KEY, apiKeySecuritySchema())) // define the apiKey SecuritySchema
                .info(new Info().title("Title API").description(
                        "RESTful services documentation with OpenAPI 3."))
                .security(Collections.singletonList(new SecurityRequirement().addList(
                        API_KEY))); // then apply it. If you don't apply it will not be added to the header in cURL
    }

    public SecurityScheme apiKeySecuritySchema() {
        return new SecurityScheme()
                .name("Authorization")
                .description("Description about the TOKEN")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.APIKEY);
    }


}
