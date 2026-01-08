package com.cocktaildb.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionAuthenticationFilter(
    private val sessionService: SessionService,
    private val customerTokenService: CustomerTokenService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        val method = request.method
        
        // Allow public endpoints (for QR code auth and static resources)
        if (isPublicEndpoint(path, method)) {
            filterChain.doFilter(request, response)
            return
        }
        
        // Check for customer authentication first (required for all access)
        val customerToken = request.getHeader("X-Customer-Token") 
            ?: request.cookies?.find { it.name == "customerToken" }?.value
        
        if (customerToken == null || !customerTokenService.validateCustomerToken(customerToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Customer authentication required")
            return
        }
        
        // For staff endpoints (admin/barkeeper), check session cookie
        if (requiresStaffAuthentication(path, method)) {
            val sessionCookie = request.cookies?.find { it.name == "sessionId" }
            val sessionId = sessionCookie?.value
            
            if (sessionId == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Staff authentication required")
                return
            }
            
            val session = sessionService.validateSession(sessionId)
            if (session == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired session")
                return
            }
        }
        
        // All authentication checks passed, continue with request
        filterChain.doFilter(request, response)
    }
    
    private fun isPublicEndpoint(path: String, method: String): Boolean {
        return path.startsWith("/api/auth/customer") || // Customer authentication endpoints
               path.startsWith("/actuator/") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs")
    }
    
    private fun requiresStaffAuthentication(path: String, method: String): Boolean {
        // Admin-only endpoints
        if (method in listOf("POST", "PUT", "DELETE")) {
            if (path.startsWith("/api/cocktails") || path.startsWith("/api/ingredients")) {
                return true
            }
        }
        
        // Settings management (admin only)
        if (path.startsWith("/api/settings") && method != "GET") {
            return true
        }
        
        // Admin and barkeeper endpoints
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/logout") || path.startsWith("/api/auth/status")) {
            return true
        }
        
        return false
    }
}
