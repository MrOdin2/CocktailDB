package com.cocktaildb.model

import jakarta.persistence.Embeddable

@Embeddable
data class CocktailIngredient(
    var ingredientId: Long,
    var measureMl: Double
)
