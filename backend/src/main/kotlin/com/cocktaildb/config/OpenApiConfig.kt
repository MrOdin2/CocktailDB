package com.cocktaildb.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("CocktailDB API")
                    .version("1.0.0")
                    .description("API for managing cocktail recipes and ingredients")
                    .contact(
                        Contact()
                            .name("CocktailDB Team")
                            .url("https://github.com/MrOdin2/CocktailDB")
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "cookieAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.COOKIE)
                            .name("sessionId")
                            .description("Session-based authentication using HTTP-only cookie")
                    )
            )
    }
}
