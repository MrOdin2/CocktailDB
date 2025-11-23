package com.cocktaildb.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordHashGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val encoder = BCryptPasswordEncoder(12)
        
        // Test the provided hash
        val providedHash = "\$2a\$12\$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYqK3GQRwu6"
        val testPassword = "admin"
        
        println("Testing password: $testPassword")
        println("Against hash: $providedHash")
        println("Matches: ${encoder.matches(testPassword, providedHash)}")
        
        // Generate new hash
        val newHash = encoder.encode(testPassword)
        println("\nNew hash for 'admin': $newHash")
        println("New hash matches: ${encoder.matches(testPassword, newHash)}")
    }
}
