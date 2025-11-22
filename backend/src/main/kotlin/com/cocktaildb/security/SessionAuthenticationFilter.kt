package com.cocktaildb.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionAuthenticationFilter(
    private val sessionService: SessionService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        val method = request.method
        
        // Allow public endpoints
        if (isPublicEndpoint(path, method)) {
            filterChain.doFilter(request, response)
            return
        }
        
        // Check for session cookie
        val sessionCookie = request.cookies?.find { it.name == "sessionId" }
        val sessionId = sessionCookie?.value
        
        if (sessionId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required")
            return
        }
        
        val session = sessionService.validateSession(sessionId)
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired session")
            return
        }
        
        // Session is valid, continue with request
        filterChain.doFilter(request, response)
    }
    
    private fun isPublicEndpoint(path: String, method: String): Boolean {
        return path.startsWith("/api/auth/") ||
               path == "/api/cocktails/available" ||
               path.startsWith("/api/cocktails/search") ||
               (path.startsWith("/api/cocktails/") && method == "GET") ||
               (path.startsWith("/api/ingredients") && method == "GET") ||
               path.startsWith("/actuator/")
    }
}
