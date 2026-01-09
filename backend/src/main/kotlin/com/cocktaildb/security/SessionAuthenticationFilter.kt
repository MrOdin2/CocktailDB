package com.cocktaildb.security

import com.cocktaildb.appsettings.AppSettingsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionAuthenticationFilter(
    private val sessionService: SessionService,
    private val customerTokenService: CustomerTokenService,
    private val appSettingsService: AppSettingsService,
    private val environment: Environment
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
        
        // Skip customer authentication in test profile
        val requireCustomerAuth = !environment.activeProfiles.contains("test") && appSettingsService.isCustomerAuthEnabled()
        
        // Check if this is a staff-only endpoint that requires staff authentication
        val isStaffEndpoint = requiresStaffAuthentication(path, method)
        
        // For staff endpoints, check staff authentication first
        if (isStaffEndpoint) {
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
            
            // Staff is authenticated, allow access without customer token
            filterChain.doFilter(request, response)
            return
        }
        
        // Check for staff session even on non-staff endpoints (staff can access anything)
        val sessionCookie = request.cookies?.find { it.name == "sessionId" }
        val sessionId = sessionCookie?.value
        if (sessionId != null) {
            val session = sessionService.validateSession(sessionId)
            if (session != null) {
                // Valid staff session - allow access without customer token
                filterChain.doFilter(request, response)
                return
            }
        }
        
        // For non-staff endpoints (visitor routes), require customer authentication
        if (requireCustomerAuth) {
            val customerToken = request.getHeader("X-Customer-Token") 
                ?: request.cookies?.find { it.name == "customerToken" }?.value
            
            if (customerToken == null || !customerTokenService.validateCustomerToken(customerToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Customer authentication required")
                return
            }
        }
        
        // All authentication checks passed, continue with request
        filterChain.doFilter(request, response)
    }
    
    private fun isPublicEndpoint(path: String, method: String): Boolean {
        return path.startsWith("/api/auth/customer") || // Customer authentication endpoints
               path.startsWith("/api/auth/login") ||  // Staff login endpoint
               path.startsWith("/actuator/") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs")
    }
    
    private fun requiresStaffAuthentication(path: String, method: String): Boolean {
        // Admin-only endpoints (CRUD operations)
        if (method in listOf("POST", "PUT", "DELETE")) {
            if (path.startsWith("/api/cocktails") || path.startsWith("/api/ingredients")) {
                return true
            }
        }
        
        // Settings management (admin only)
        if (path.startsWith("/api/settings") && method != "GET") {
            return true
        }
        
        // Customer token generation (admin only) - requires admin session
        if (path.startsWith("/api/auth/customer/generate-token")) {
            return true
        }
        
        return false
    }
}
