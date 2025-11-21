package com.cocktaildb.model

import jakarta.persistence.*

@Entity
@Table(name = "cocktails")
data class Cocktail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    var name: String,
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocktail_ingredients", joinColumns = [JoinColumn(name = "cocktail_id")])
    var ingredients: MutableList<CocktailIngredient> = mutableListOf(),
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocktail_steps", joinColumns = [JoinColumn(name = "cocktail_id")])
    @Column(name = "step")
    var steps: MutableList<String> = mutableListOf(),
    
    @Column(length = 1000)
    var notes: String? = null,
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocktail_tags", joinColumns = [JoinColumn(name = "cocktail_id")])
    @Column(name = "tag")
    var tags: MutableList<String> = mutableListOf(),
    
    @Column(nullable = false)
    var abv: Int = 0,
    
    @Column(nullable = false, length = 50)
    var baseSpirit: String = "none"
)
