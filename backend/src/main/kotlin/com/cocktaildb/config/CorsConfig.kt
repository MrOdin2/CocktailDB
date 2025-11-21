package com.cocktaildb.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
    
    @Value("\${cors.allowed-origins:*}")
    private lateinit var allowedOrigins: String
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        
        // Parse allowed origins from comma-separated string
        val origins = if (allowedOrigins == "*") {
            listOf("*")
        } else {
            allowedOrigins.split(",").map { it.trim() }
        }
        
        // Use allowedOriginPatterns instead of deprecated allowedOrigins
        configuration.allowedOriginPatterns = origins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        // allowCredentials must be false when using wildcard origins
        configuration.allowCredentials = origins.none { it == "*" }
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
