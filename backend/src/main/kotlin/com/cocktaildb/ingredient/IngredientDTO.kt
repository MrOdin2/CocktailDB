package com.cocktaildb.ingredient

/**
 * Data Transfer Object for Ingredient
 * Used to avoid circular references when serializing ingredient relationships
 */
data class IngredientDTO(
    val id: Long?,
    val name: String,
    val type: IngredientType,
    val abv: Int,
    val inStock: Boolean,
    val substituteIds: Set<Long> = emptySet(),
    val alternativeIds: Set<Long> = emptySet()
)

/**
 * Response object for cocktails available with substitutions
 */
data class CocktailsWithSubstitutionsResponse(
    val exact: List<com.cocktaildb.cocktail.Cocktail>,
    val withSubstitutes: List<com.cocktaildb.cocktail.Cocktail>,
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
