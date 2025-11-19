package com.cocktaildb.controller

import com.cocktaildb.security.PasswordService
import com.cocktaildb.security.SessionService
import com.cocktaildb.security.UserRole
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoginRequest(
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val role: String? = null
)

data class AuthStatusResponse(
    val authenticated: Boolean,
    val role: String? = null
)

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:4200"], allowCredentials = "true")
class AuthController(
    private val passwordService: PasswordService,
    private val sessionService: SessionService,
    @Value("\${admin.password.hash}")
    private val adminPasswordHash: String
) {
    
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponse> {
        // Verify admin password
        if (!passwordService.verifyPassword(request.password, adminPasswordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse(success = false, message = "Invalid password"))
        }
        
        // Create session
        val sessionId = sessionService.createSession(UserRole.ADMIN)
        
        // Set session cookie
        val cookie = Cookie("sessionId", sessionId).apply {
            isHttpOnly = true
            secure = false // Set to true in production with HTTPS
            path = "/"
            maxAge = 60 * 60 * 24 // 24 hours
        }
        response.addCookie(cookie)
        
        return ResponseEntity.ok(LoginResponse(
            success = true,
            message = "Login successful",
            role = "ADMIN"
        ))
    }
    
    @PostMapping("/logout")
    fun logout(
        @CookieValue(value = "sessionId", required = false) sessionId: String?,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponse> {
        if (sessionId != null) {
            sessionService.terminateSession(sessionId)
        }
        
        // Clear session cookie
        val cookie = Cookie("sessionId", "").apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
        
        return ResponseEntity.ok(LoginResponse(success = true, message = "Logout successful"))
    }
    
    @GetMapping("/status")
    fun getAuthStatus(
        @CookieValue(value = "sessionId", required = false) sessionId: String?
    ): ResponseEntity<AuthStatusResponse> {
        if (sessionId == null) {
            return ResponseEntity.ok(AuthStatusResponse(authenticated = false))
        }
        
        val session = sessionService.validateSession(sessionId)
        return if (session != null) {
            ResponseEntity.ok(AuthStatusResponse(
                authenticated = true,
                role = session.role.name
            ))
        } else {
            ResponseEntity.ok(AuthStatusResponse(authenticated = false))
        }
    }
}
