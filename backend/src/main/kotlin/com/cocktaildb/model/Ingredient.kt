package com.cocktaildb.model

import jakarta.persistence.*

@Entity
@Table(name = "ingredients")
data class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    var name: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: IngredientType,
    
    @Column(nullable = false)
    var abv: Int = 0,
    
    @Column(nullable = false)
    var inStock: Boolean = false
)
