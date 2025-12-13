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

        cocktail.abv = calculateAbv(cocktail.ingredients)
        cocktail.baseSpirit = determineBaseSpirit(cocktail.ingredients)

        return cocktailDataService.createCocktail(cocktail)
    }

    fun updateCocktail(id: Long, cocktail: Cocktail): Cocktail? {
        val existing = cocktailDataService.getCocktailById(id) ?: return null
        existing.name = cocktail.name
        existing.ingredients = cocktail.ingredients
        existing.steps = cocktail.steps
        existing.notes = cocktail.notes
        existing.tags = cocktail.tags

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

}