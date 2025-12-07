package com.cocktaildb.controller

import com.cocktaildb.security.PasswordService
import com.cocktaildb.security.SessionService
import com.cocktaildb.security.UserRole
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import jakarta.servlet.http.Cookie

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @Autowired
    private lateinit var sessionService: SessionService
    
    @Autowired
    private lateinit var passwordService: PasswordService
    
    @Test
    fun `login should succeed with correct admin password`() {
        // Given
        val loginRequest = mapOf(
            "password" to "admin",
            "role" to "ADMIN"
        )
        
        // When/Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andExpect(cookie().exists("sessionId"))
    }
    
    @Test
    fun `login should succeed with correct barkeeper password`() {
        // Given
        val loginRequest = mapOf(
            "password" to "barkeeper",
            "role" to "BARKEEPER"
        )
        
        // When/Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.role").value("BARKEEPER"))
            .andExpect(cookie().exists("sessionId"))
    }
    
    @Test
    fun `login should fail with incorrect password`() {
        // Given
        val loginRequest = mapOf(
            "password" to "wrongpassword",
            "role" to "ADMIN"
        )
        
        // When/Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.success").value(false))
    }
    
    @Test
    fun `logout should clear session cookie`() {
        // Given - create a session
        val sessionId = sessionService.createSession(UserRole.ADMIN)
        
        // When/Then
        mockMvc.perform(
            post("/api/auth/logout")
                .cookie(Cookie("sessionId", sessionId))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(cookie().maxAge("sessionId", 0))
    }
    
    @Test
    fun `logout should succeed even without session cookie`() {
        // When/Then
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }
    
    @Test
    fun `getAuthStatus should return authenticated true for valid session`() {
        // Given - create a session
        val sessionId = sessionService.createSession(UserRole.ADMIN)
        
        // When/Then
        mockMvc.perform(
            get("/api/auth/status")
                .cookie(Cookie("sessionId", sessionId))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.authenticated").value(true))
            .andExpect(jsonPath("$.role").value("ADMIN"))
    }
    
    @Test
    fun `getAuthStatus should return authenticated false for invalid session`() {
        // When/Then
        mockMvc.perform(
            get("/api/auth/status")
                .cookie(Cookie("sessionId", "invalid-session-id"))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.authenticated").value(false))
    }
    
    @Test
    fun `getAuthStatus should return authenticated false without session cookie`() {
        // When/Then
        mockMvc.perform(get("/api/auth/status"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.authenticated").value(false))
    }
}
