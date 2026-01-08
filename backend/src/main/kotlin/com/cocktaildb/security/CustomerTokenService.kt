package com.cocktaildb.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class CustomerTokenService(
    @Value("\${customer.token.secret:default-customer-secret-change-in-production}")
    private val tokenSecret: String
) {
    
    /**
     * Generate a customer authentication token
     * Token format: base64(timestamp:hmac)
     */
    fun generateCustomerToken(): String {
        val timestamp = Instant.now().epochSecond
        val message = "customer:$timestamp"
        val hmac = generateHMAC(message)
        val tokenData = "$timestamp:$hmac"
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenData.toByteArray())
    }
    
    /**
     * Validate a customer authentication token
     * Returns true if token is valid and not expired (24 hours)
     */
    fun validateCustomerToken(token: String): Boolean {
        try {
            val decoded = String(Base64.getUrlDecoder().decode(token))
            val parts = decoded.split(":")
            if (parts.size != 2) return false
            
            val timestamp = parts[0].toLongOrNull() ?: return false
            val providedHmac = parts[1]
            
            // Check expiration (24 hours = 86400 seconds)
            val now = Instant.now().epochSecond
            if (now - timestamp > 86400) return false
            
            // Verify HMAC
            val message = "customer:$timestamp"
            val expectedHmac = generateHMAC(message)
            
            return providedHmac == expectedHmac
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Generate HMAC-SHA256 signature
     */
    private fun generateHMAC(message: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(tokenSecret.toByteArray(), "HmacSHA256")
        mac.init(secretKey)
        val hmacBytes = mac.doFinal(message.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes)
    }
}
