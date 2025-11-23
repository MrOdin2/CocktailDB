package com.cocktaildb.model

import jakarta.persistence.*

@Entity
@Table(name = "app_settings")
data class AppSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    var settingKey: String,
    
    @Column(nullable = false)
    var settingValue: String
)
