package com.cocktaildb.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService {
    private val encoder = BCryptPasswordEncoder(12)
    
    fun verifyPassword(plaintext: String, hash: String): Boolean {
        return encoder.matches(plaintext, hash)
    }
    
    fun hashPassword(plaintext: String): String {
        return encoder.encode(plaintext)
    }
}
