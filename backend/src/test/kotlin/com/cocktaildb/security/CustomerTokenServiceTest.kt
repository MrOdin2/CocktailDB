package com.cocktaildb.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
class CustomerTokenServiceTest {
    
    private lateinit var customerTokenService: CustomerTokenService
    
    @BeforeEach
    fun setup() {
        customerTokenService = CustomerTokenService("test-secret-key-for-testing")
    }
    
    @Test
    fun `should generate valid customer token`() {
        // When
        val token = customerTokenService.generateCustomerToken()
        
        // Then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }
    
    @Test
    fun `should validate newly generated token`() {
        // Given
        val token = customerTokenService.generateCustomerToken()
        
        // When
        val isValid = customerTokenService.validateCustomerToken(token)
        
        // Then
        assertTrue(isValid)
    }
    
    @Test
    fun `should reject invalid token format`() {
        // Given
        val invalidToken = "invalid-token-format"
        
        // When
        val isValid = customerTokenService.validateCustomerToken(invalidToken)
        
        // Then
        assertFalse(isValid)
    }
    
    @Test
    fun `should reject empty token`() {
        // Given
        val emptyToken = ""
        
        // When
        val isValid = customerTokenService.validateCustomerToken(emptyToken)
        
        // Then
        assertFalse(isValid)
    }
    
    @Test
    fun `should reject token with invalid HMAC`() {
        // Given
        val token = customerTokenService.generateCustomerToken()
        // Tamper with the token by replacing a character
        val tamperedToken = token.dropLast(1) + "X"
        
        // When
        val isValid = customerTokenService.validateCustomerToken(tamperedToken)
        
        // Then
        assertFalse(isValid)
    }
    
    @Test
    fun `should generate different tokens on each call`() {
        // Given
        Thread.sleep(1000) // Wait 1 second to ensure different timestamp
        
        // When
        val token1 = customerTokenService.generateCustomerToken()
        Thread.sleep(1000)
        val token2 = customerTokenService.generateCustomerToken()
        
        // Then
        assertNotEquals(token1, token2)
    }
}
