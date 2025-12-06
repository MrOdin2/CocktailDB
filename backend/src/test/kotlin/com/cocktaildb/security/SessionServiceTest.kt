package com.cocktaildb.security

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.Instant

class SessionServiceTest {
    
    private lateinit var sessionService: SessionService
    
    @BeforeEach
    fun setup() {
        sessionService = SessionService(sessionTimeoutMinutes = 60)
    }
    
    @Test
    fun `createSession should create valid session for admin`() {
        // When
        val sessionId = sessionService.createSession(UserRole.ADMIN)
        
        // Then
        assertNotNull(sessionId)
        assertTrue(sessionId.isNotBlank())
        
        val session = sessionService.validateSession(sessionId)
        assertNotNull(session)
        assertEquals(UserRole.ADMIN, session?.role)
    }
    
    @Test
    fun `createSession should create valid session for barkeeper`() {
        // When
        val sessionId = sessionService.createSession(UserRole.BARKEEPER)
        
        // Then
        assertNotNull(sessionId)
        assertTrue(sessionId.isNotBlank())
        
        val session = sessionService.validateSession(sessionId)
        assertNotNull(session)
        assertEquals(UserRole.BARKEEPER, session?.role)
    }
    
    @Test
    fun `createSession should generate unique session IDs`() {
        // When
        val sessionId1 = sessionService.createSession(UserRole.ADMIN)
        val sessionId2 = sessionService.createSession(UserRole.ADMIN)
        
        // Then
        assertNotEquals(sessionId1, sessionId2)
    }
    
    @Test
    fun `validateSession should return null for non-existent session`() {
        // When
        val session = sessionService.validateSession("non-existent-id")
        
        // Then
        assertNull(session)
    }
    
    @Test
    fun `validateSession should update last access time`() {
        // Given
        val sessionId = sessionService.createSession(UserRole.ADMIN)
        val session1 = sessionService.validateSession(sessionId)
        val firstAccessTime = session1?.lastAccessAt
        
        // Wait a bit
        Thread.sleep(100)
        
        // When
        val session2 = sessionService.validateSession(sessionId)
        val secondAccessTime = session2?.lastAccessAt
        
        // Then
        assertNotNull(firstAccessTime)
        assertNotNull(secondAccessTime)
        assertTrue(secondAccessTime!! > firstAccessTime!!)
    }
    
    @Test
    fun `terminateSession should remove session`() {
        // Given
        val sessionId = sessionService.createSession(UserRole.ADMIN)
        assertNotNull(sessionService.validateSession(sessionId))
        
        // When
        sessionService.terminateSession(sessionId)
        
        // Then
        assertNull(sessionService.validateSession(sessionId))
    }
    
    @Test
    fun `terminateSession should be idempotent for non-existent session`() {
        // When/Then - should not throw exception
        assertDoesNotThrow {
            sessionService.terminateSession("non-existent-id")
        }
    }
    
    @Test
    fun `validateSession should return null for expired session`() {
        // Given - create service with very short timeout (1 second)
        val shortSessionService = SessionService(sessionTimeoutMinutes = 0)
        val sessionId = shortSessionService.createSession(UserRole.ADMIN)
        
        // Wait for expiration (2 seconds to ensure timeout)
        Thread.sleep(2000)
        
        // When
        val session = shortSessionService.validateSession(sessionId)
        
        // Then
        assertNull(session)
    }
}
