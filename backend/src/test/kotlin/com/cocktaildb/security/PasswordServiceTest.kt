package com.cocktaildb.security

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PasswordServiceTest {
    
    private val passwordService = PasswordService()
    
    @Test
    fun `hashPassword should generate valid hash`() {
        // Given
        val plaintext = "testPassword123"
        
        // When
        val hash = passwordService.hashPassword(plaintext)
        
        // Then
        assertNotNull(hash)
        assertNotEquals(plaintext, hash)
        assertTrue(hash.startsWith("\$2a\$12\$")) // BCrypt with cost factor 12
    }
    
    @Test
    fun `hashPassword should generate different hashes for same password`() {
        // Given
        val plaintext = "testPassword123"
        
        // When
        val hash1 = passwordService.hashPassword(plaintext)
        val hash2 = passwordService.hashPassword(plaintext)
        
        // Then
        assertNotEquals(hash1, hash2) // BCrypt uses salt, so hashes differ
    }
    
    @Test
    fun `verifyPassword should return true for correct password`() {
        // Given
        val plaintext = "testPassword123"
        val hash = passwordService.hashPassword(plaintext)
        
        // When
        val result = passwordService.verifyPassword(plaintext, hash)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `verifyPassword should return false for incorrect password`() {
        // Given
        val plaintext = "testPassword123"
        val wrongPassword = "wrongPassword"
        val hash = passwordService.hashPassword(plaintext)
        
        // When
        val result = passwordService.verifyPassword(wrongPassword, hash)
        
        // Then
        assertFalse(result)
    }
}
