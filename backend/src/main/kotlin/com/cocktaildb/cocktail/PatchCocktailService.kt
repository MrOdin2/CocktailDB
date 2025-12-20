package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.IngredientDataService
import com.cocktaildb.ingredient.IngredientType
import org.springframework.stereotype.Service

@Service
class PatchCocktailService(
    private val cocktailDataService: CocktailDataService,
    private val ingredientDataService: IngredientDataService,
) {

    fun createCocktail(cocktail: Cocktail): Cocktail {
        // Validate variation reference
        validateVariationReference(null, cocktail.variationOfId)

        cocktail.abv = calculateAbv(cocktail.ingredients)
        cocktail.baseSpirit = determineBaseSpirit(cocktail.ingredients)

        return cocktailDataService.createCocktail(cocktail)
    }

    fun updateCocktail(id: Long, cocktail: Cocktail): Cocktail? {
        val existing = cocktailDataService.getCocktailById(id) ?: return null
        
        // Validate variation reference
        validateVariationReference(id, cocktail.variationOfId)
        
        existing.name = cocktail.name
        existing.ingredients = cocktail.ingredients
        existing.steps = cocktail.steps
        existing.notes = cocktail.notes
        existing.tags = cocktail.tags
        existing.glasswareTypes = cocktail.glasswareTypes
        existing.iceTypes = cocktail.iceTypes
        existing.variationOfId = cocktail.variationOfId

        existing.abv = calculateAbv(cocktail.ingredients)
        existing.baseSpirit = determineBaseSpirit(cocktail.ingredients)

        return cocktailDataService.updateCocktail(id, existing)
    }

    fun calculateAbv(ingredients: List<CocktailIngredient>): Int {
        // Filter out non-volume measures (negative values represent item counts, not volume)
        val volumeIngredients = ingredients.filter { it.measureMl >= 0 }
        
        val totalMl = volumeIngredients.sumOf { it.measureMl }
        if (totalMl == 0.0) return 0

        val weightedAbv = volumeIngredients.sumOf { cocktailIngredient ->
            val ingredient = ingredientDataService.getIngredientById(cocktailIngredient.ingredientId)
            (ingredient?.abv ?: 0) * cocktailIngredient.measureMl
        }

        return (weightedAbv / totalMl).toInt()
    }

    fun determineBaseSpirit(ingredients: List<CocktailIngredient>): String {
        if (ingredients.isEmpty()) return "Unknown"

        // Filter out non-volume measures (negative values represent item counts, not volume)
        val volumeIngredients = ingredients.filter { it.measureMl >= 0 }
        
        val ingredientWithMeasure = volumeIngredients.map { cocktailIngredient ->
            val ingredient = ingredientDataService.getIngredientById(cocktailIngredient.ingredientId)
            ingredient to cocktailIngredient.measureMl
        }

        // First, try to find the spirit with the highest volume
        val baseSpirit = ingredientWithMeasure
            .filter { (ingredient, _) -> ingredient?.type == IngredientType.SPIRIT }
            .maxByOrNull { (_, measureMl) -> measureMl }
            ?.first

        // If no spirits, use the ingredient with highest volume
        return baseSpirit?.name
            ?: "none"
    }

    /**
     * Validates that the variation reference does not create circular dependencies.
     * @param cocktailId The ID of the cocktail being created/updated (null for create)
     * @param variationOfId The ID of the base cocktail
     * @throws IllegalArgumentException if the reference creates a circular dependency
     */
    private fun validateVariationReference(cocktailId: Long?, variationOfId: Long?) {
        // No validation needed if no variation is set
        if (variationOfId == null) return
        
        // Cannot reference itself
        if (cocktailId != null && cocktailId == variationOfId) {
            throw IllegalArgumentException("A cocktail cannot be a variation of itself")
        }
        
        // Check if base cocktail exists
        val baseCocktail = cocktailDataService.getCocktailById(variationOfId)
            ?: throw IllegalArgumentException("Base cocktail with ID $variationOfId does not exist")
        
        // Check for circular references by traversing the chain
        val visited = mutableSetOf<Long>()
        var currentId: Long? = variationOfId
        
        while (currentId != null) {
            if (currentId == cocktailId) {
                throw IllegalArgumentException("Circular reference detected: cocktail $cocktailId is already in the variation chain")
            }
            
            if (visited.contains(currentId)) {
                // Already visited this node, circular reference exists in the chain but doesn't involve current cocktail
                break
            }
            
            visited.add(currentId)
            val current = cocktailDataService.getCocktailById(currentId)
            currentId = current?.variationOfId
        }
    }

}