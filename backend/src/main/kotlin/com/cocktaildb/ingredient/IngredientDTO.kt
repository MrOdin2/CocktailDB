package com.cocktaildb.ingredient

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for Ingredient
 * Used to avoid circular references when serializing ingredient relationships
 */
@Schema(description = "Ingredient data transfer object with substitute and alternative relationships")
data class IngredientDTO(
    @Schema(description = "Unique identifier", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    val id: Long?,
    @Schema(description = "Ingredient name", required = true, example = "White Rum")
    val name: String,
    @Schema(description = "Type of ingredient", required = true, example = "SPIRIT")
    val type: IngredientType,
    @Schema(description = "Alcohol by volume percentage", required = true, example = "40")
    val abv: Int,
    @Schema(description = "Whether the ingredient is currently in stock", required = true, example = "true")
    val inStock: Boolean,
    @Schema(description = "IDs of ingredients that can substitute for this one", example = "[2, 3]")
    val substituteIds: Set<Long> = emptySet(),
    @Schema(description = "IDs of alternative ingredients", example = "[4, 5]")
    val alternativeIds: Set<Long> = emptySet()
)

/**
 * Response object for cocktails available with substitutions
 */
@Schema(description = "Cocktails categorized by ingredient availability")
data class CocktailsWithSubstitutionsResponse(
    @Schema(description = "Cocktails that can be made with exact in-stock ingredients")
    val exact: List<com.cocktaildb.cocktail.Cocktail>,
    @Schema(description = "Cocktails that can be made with ingredient substitutes")
    val withSubstitutes: List<com.cocktaildb.cocktail.Cocktail>,
    @Schema(description = "Cocktails that can be made with alternative ingredients")
    val withAlternatives: List<com.cocktaildb.cocktail.Cocktail>
)

/**
 * Extension function to convert Ingredient entity to DTO
 */
fun Ingredient.toDTO(): IngredientDTO {
    return IngredientDTO(
        id = this.id,
        name = this.name,
        type = this.type,
        abv = this.abv,
        inStock = this.inStock,
        substituteIds = this.substitutes.mapNotNull { it.id }.toSet(),
        alternativeIds = this.alternatives.mapNotNull { it.id }.toSet()
    )
}

/**
 * Extension function to convert a list of Ingredient entities to DTOs
 */
fun List<Ingredient>.toDTOs(): List<IngredientDTO> {
    return this.map { it.toDTO() }
}
