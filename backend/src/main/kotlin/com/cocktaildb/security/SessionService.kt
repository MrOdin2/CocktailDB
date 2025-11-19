package com.cocktaildb.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
    @Value("\${session.timeout.minutes:60}")
    private val sessionTimeoutMinutes: Long
) {
    private val sessions = ConcurrentHashMap<String, Session>()
    
    fun createSession(role: UserRole): String {
        val sessionId = UUID.randomUUID().toString()
        val now = Instant.now()
        val session = Session(
            sessionId = sessionId,
            role = role,
            createdAt = now,
            lastAccessAt = now
        )
        sessions[sessionId] = session
        return sessionId
    }
    
    fun validateSession(sessionId: String): Session? {
        val session = sessions[sessionId] ?: return null
        
        // Check if session has expired
        val now = Instant.now()
        val timeoutSeconds = sessionTimeoutMinutes * 60
        if (now.epochSecond - session.lastAccessAt.epochSecond > timeoutSeconds) {
            sessions.remove(sessionId)
            return null
        }
        
        // Update last access time
        session.lastAccessAt = now
        return session
    }
    
    fun terminateSession(sessionId: String) {
        sessions.remove(sessionId)
    }
    
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    fun cleanupExpiredSessions() {
        val now = Instant.now()
        val timeoutSeconds = sessionTimeoutMinutes * 60
        
        sessions.entries.removeIf { (_, session) ->
            now.epochSecond - session.lastAccessAt.epochSecond > timeoutSeconds
        }
    }
}
