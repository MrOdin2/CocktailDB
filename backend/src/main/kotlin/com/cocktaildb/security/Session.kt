package com.cocktaildb.security

import java.time.Instant

data class Session(
    val sessionId: String,
    val role: UserRole,
    val createdAt: Instant,
    var lastAccessAt: Instant
)
