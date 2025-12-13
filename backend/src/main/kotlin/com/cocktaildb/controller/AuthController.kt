package com.cocktaildb.controller

import com.cocktaildb.security.PasswordService
import com.cocktaildb.security.SessionService
import com.cocktaildb.security.UserRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Schema(description = "Login request with password and optional role")
data class LoginRequest(
    @Schema(description = "User password", required = true, example = "admin123")
    val password: String,
    @Schema(description = "User role (ADMIN or BARKEEPER). Defaults to ADMIN if not specified.", example = "ADMIN")
    val role: String? = null
)

@Schema(description = "Login response with success status and user information")
data class LoginResponse(
    @Schema(description = "Whether the login was successful", required = true)
    val success: Boolean,
    @Schema(description = "Message describing the result")
    val message: String? = null,
    @Schema(description = "User role if login was successful")
    val role: String? = null
)

@Schema(description = "Authentication status response")
data class AuthStatusResponse(
    @Schema(description = "Whether the user is authenticated", required = true)
    val authenticated: Boolean,
    @Schema(description = "User role if authenticated")
    val role: String? = null
)

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:4200"], allowCredentials = "true")
@Tag(name = "Authentication", description = "Authentication and session management endpoints")
class AuthController(
    private val passwordService: PasswordService,
    private val sessionService: SessionService,
    @Value("\${admin.password.hash}")
    private val adminPasswordHash: String,
    @Value("\${barkeeper.password.hash}")
    private val barkeeperPasswordHash: String
) {
    
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticate user with password and create a session. Returns session cookie."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login successful",
                content = [Content(schema = Schema(implementation = LoginResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid password",
                content = [Content(schema = Schema(implementation = LoginResponse::class))]
            )
        ]
    )
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponse> {
        // Determine which role to authenticate
        val role = when (request.role?.uppercase()) {
            "BARKEEPER" -> UserRole.BARKEEPER
            else -> UserRole.ADMIN
        }
        
        // Verify password based on role
        val passwordHash = when (role) {
            UserRole.ADMIN -> adminPasswordHash
            UserRole.BARKEEPER -> barkeeperPasswordHash
        }
        
        if (!passwordService.verifyPassword(request.password, passwordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse(success = false, message = "Invalid password"))
        }
        
        // Create session
        val sessionId = sessionService.createSession(role)
        
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
            role = role.name
        ))
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Terminate the current session and clear the session cookie"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Logout successful",
                content = [Content(schema = Schema(implementation = LoginResponse::class))]
            )
        ]
    )
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
    @Operation(
        summary = "Get authentication status",
        description = "Check if the current session is authenticated and return user role"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Authentication status retrieved",
                content = [Content(schema = Schema(implementation = AuthStatusResponse::class))]
            )
        ]
    )
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
