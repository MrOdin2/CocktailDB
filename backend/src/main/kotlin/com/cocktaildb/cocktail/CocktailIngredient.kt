package com.cocktaildb.cocktail

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Embeddable

@Embeddable
@Schema(description = "Ingredient reference with measurement for a cocktail recipe")
data class CocktailIngredient(
    @Schema(description = "ID of the ingredient", required = true, example = "1")
    var ingredientId: Long,
    @Schema(description = "Measurement in milliliters. Use negative values for count-based items (e.g., -2 for 2 pieces)", required = true, example = "60.0")
    var measureMl: Double
)