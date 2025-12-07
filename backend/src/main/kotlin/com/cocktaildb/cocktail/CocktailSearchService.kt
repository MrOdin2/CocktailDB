package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.CocktailsWithSubstitutionsResponse
import com.cocktaildb.ingredient.IngredientDataService
import org.springframework.stereotype.Service

@Service
class CocktailSearchService(
    private val cocktailDataService: CocktailDataService,
    private val ingredientDataService: IngredientDataService,
) {

    fun getAvailableCocktails(): List<Cocktail> {
        val inStockIngredients = ingredientDataService.getInStockIngredients()
        val inStockIngredientIds = inStockIngredients.mapNotNull { it.id }.toSet()

        val allCocktails = cocktailDataService.getAll()

        return allCocktails.filter { cocktail ->
            val requiredIngredientIds = cocktail.ingredients.map { it.ingredientId }.toSet()
            inStockIngredientIds.containsAll(requiredIngredientIds)
        }
    }

    /**
     * Get cocktails available with exact ingredients, substitutes, or alternatives
     * Returns a structured response with categories: "exact", "withSubstitutes", "withAlternatives"
     */
    fun getAvailableCocktailsWithSubstitutions(): CocktailsWithSubstitutionsResponse {
        val inStockIngredients = ingredientDataService.getInStockIngredients()
        val inStockIngredientIds = inStockIngredients.mapNotNull { it.id }.toSet()
        
        // Build a map of available ingredients including substitutes and alternatives
        val availableWithSubstitutes = mutableSetOf<Long>()
        val availableWithAlternatives = mutableSetOf<Long>()
        
        availableWithSubstitutes.addAll(inStockIngredientIds)
        availableWithAlternatives.addAll(inStockIngredientIds)
        
        inStockIngredients.forEach { ingredient ->
            // Add substitutes (bidirectional)
            ingredient.substitutes.mapNotNull { it.id }.forEach { subId ->
                availableWithSubstitutes.add(subId)
            }
            // Add alternatives (bidirectional)
            ingredient.alternatives.mapNotNull { it.id }.forEach { altId ->
                availableWithAlternatives.add(altId)
            }
        }

        val allCocktails = cocktailDataService.getAll()
        
        val exactCocktails = mutableListOf<Cocktail>()
        val withSubstitutes = mutableListOf<Cocktail>()
        val withAlternatives = mutableListOf<Cocktail>()

        allCocktails.forEach { cocktail ->
            val requiredIds = cocktail.ingredients.map { it.ingredientId }.toSet()
            
            when {
                inStockIngredientIds.containsAll(requiredIds) -> exactCocktails.add(cocktail)
                availableWithSubstitutes.containsAll(requiredIds) -> withSubstitutes.add(cocktail)
                availableWithAlternatives.containsAll(requiredIds) -> withAlternatives.add(cocktail)
            }
        }

        return CocktailsWithSubstitutionsResponse(
            exact = exactCocktails,
            withSubstitutes = withSubstitutes,
            withAlternatives = withAlternatives
        )
    }

    fun searchCocktails(name: String? = null, spirit: String? = null, tags: List<String>? = null): List<Cocktail> {
        val allCocktails = cocktailDataService.getAll()

        return allCocktails.filter { cocktail ->
            var matches = true

            // Filter by name (case-insensitive partial match)
            if (!name.isNullOrBlank()) {
                matches = matches && cocktail.name.contains(name, ignoreCase = true)
            }

            // Filter by spirit (check if any ingredient is the specified spirit)
            if (!spirit.isNullOrBlank()) {
                val hasSpirit = cocktail.ingredients.any { cocktailIng ->
                    val ingredient = ingredientDataService.getIngredientById(cocktailIng.ingredientId)
                    ingredient != null && ingredient.name.equals(spirit, ignoreCase = true)
                }
                matches = matches && hasSpirit
            }

            // Filter by tags (cocktail must have all specified tags)
            if (!tags.isNullOrEmpty()) {
                val cocktailTags = cocktail.tags.map { it.lowercase() }
                matches = matches && tags.all { tag -> cocktailTags.contains(tag.lowercase()) }
            }

            matches
        }
    }
}