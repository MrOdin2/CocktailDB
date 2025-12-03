package com.cocktaildb.service

import com.cocktaildb.model.Cocktail
import com.cocktaildb.repository.CocktailRepository
import com.cocktaildb.repository.IngredientRepository
import org.springframework.stereotype.Service

/**
 * Availability status for a cocktail based on ingredient stock and substitutions.
 */
enum class CocktailAvailability {
    AVAILABLE,             // All ingredients in stock
    AVAILABLE_WITH_SUBSTITUTES, // Available using substitutes (nearly identical ingredients)
    AVAILABLE_WITH_ALTERNATIVES, // Available using alternatives (noticeably different)
    UNAVAILABLE            // Missing ingredients without substitutes/alternatives
}

/**
 * Information about a cocktail's availability including details about which
 * ingredients need substitutes or alternatives.
 */
data class CocktailAvailabilityInfo(
    val cocktail: Cocktail,
    val availability: CocktailAvailability,
    val substitutionsNeeded: Map<Long, Long> = emptyMap(), // originalIngredientId -> substituteId
    val alternativesNeeded: Map<Long, Long> = emptyMap()   // originalIngredientId -> alternativeId
)

@Service
class CocktailService(
    private val cocktailRepository: CocktailRepository,
    private val ingredientRepository: IngredientRepository
) {
    
    fun getAllCocktails(): List<Cocktail> {
        return cocktailRepository.findAll()
    }
    
    fun getCocktailById(id: Long): Cocktail? {
        return cocktailRepository.findById(id).orElse(null)
    }
    
    fun createCocktail(cocktail: Cocktail): Cocktail {
        return cocktailRepository.save(cocktail)
    }
    
    fun updateCocktail(id: Long, cocktail: Cocktail): Cocktail? {
        val existing = cocktailRepository.findById(id).orElse(null) ?: return null
        existing.name = cocktail.name
        existing.ingredients = cocktail.ingredients
        existing.steps = cocktail.steps
        existing.notes = cocktail.notes
        existing.tags = cocktail.tags
        existing.abv = cocktail.abv
        existing.baseSpirit = cocktail.baseSpirit
        return cocktailRepository.save(existing)
    }
    
    fun deleteCocktail(id: Long) {
        cocktailRepository.deleteById(id)
    }
    
    /**
     * Get cocktails that are available with original ingredients only.
     */
    fun getAvailableCocktails(): List<Cocktail> {
        val inStockIngredients = ingredientRepository.findByInStock(true)
        val inStockIngredientIds = inStockIngredients.mapNotNull { it.id }.toSet()
        
        val allCocktails = cocktailRepository.findAll()
        
        return allCocktails.filter { cocktail ->
            val requiredIngredientIds = cocktail.ingredients.map { it.ingredientId }.toSet()
            inStockIngredientIds.containsAll(requiredIngredientIds)
        }
    }
    
    /**
     * Get all cocktails with their availability information considering substitutes and alternatives.
     */
    fun getAvailableCocktailsWithSubstitutions(): List<CocktailAvailabilityInfo> {
        val allIngredients = ingredientRepository.findAll()
        val ingredientMap = allIngredients.associateBy { it.id }
        val inStockIds = allIngredients.filter { it.inStock }.mapNotNull { it.id }.toSet()
        
        val allCocktails = cocktailRepository.findAll()
        
        return allCocktails.map { cocktail ->
            val requiredIds = cocktail.ingredients.map { it.ingredientId }.toSet()
            val missingIds = requiredIds - inStockIds
            
            if (missingIds.isEmpty()) {
                // All ingredients in stock
                CocktailAvailabilityInfo(cocktail, CocktailAvailability.AVAILABLE)
            } else {
                // Check if missing ingredients have in-stock substitutes or alternatives
                val substitutionsNeeded = mutableMapOf<Long, Long>()
                val alternativesNeeded = mutableMapOf<Long, Long>()
                val unresolvable = mutableSetOf<Long>()
                
                for (missingId in missingIds) {
                    val ingredient = ingredientMap[missingId]
                    if (ingredient != null) {
                        // First, try to find an in-stock substitute
                        val inStockSubstitute = ingredient.substituteIds.firstOrNull { it in inStockIds }
                        if (inStockSubstitute != null) {
                            substitutionsNeeded[missingId] = inStockSubstitute
                        } else {
                            // Then, try to find an in-stock alternative
                            val inStockAlternative = ingredient.alternativeIds.firstOrNull { it in inStockIds }
                            if (inStockAlternative != null) {
                                alternativesNeeded[missingId] = inStockAlternative
                            } else {
                                unresolvable.add(missingId)
                            }
                        }
                    } else {
                        unresolvable.add(missingId)
                    }
                }
                
                when {
                    unresolvable.isNotEmpty() -> {
                        CocktailAvailabilityInfo(cocktail, CocktailAvailability.UNAVAILABLE)
                    }
                    alternativesNeeded.isNotEmpty() -> {
                        CocktailAvailabilityInfo(
                            cocktail,
                            CocktailAvailability.AVAILABLE_WITH_ALTERNATIVES,
                            substitutionsNeeded,
                            alternativesNeeded
                        )
                    }
                    substitutionsNeeded.isNotEmpty() -> {
                        CocktailAvailabilityInfo(
                            cocktail,
                            CocktailAvailability.AVAILABLE_WITH_SUBSTITUTES,
                            substitutionsNeeded,
                            emptyMap()
                        )
                    }
                    else -> {
                        CocktailAvailabilityInfo(cocktail, CocktailAvailability.UNAVAILABLE)
                    }
                }
            }
        }
    }
    
    /**
     * Get counts of cocktails unlocked by adding each ingredient to stock.
     * Returns a map of ingredient ID to a pair of (newAvailable, newAvailableAsAlternatives).
     */
    fun getIngredientImpact(): Map<Long, Pair<Int, Int>> {
        val allIngredients = ingredientRepository.findAll()
        val ingredientMap = allIngredients.associateBy { it.id }
        val outOfStockIngredients = allIngredients.filter { !it.inStock }
        val result = mutableMapOf<Long, Pair<Int, Int>>()
        
        // Pre-calculate base in-stock IDs once
        val baseInStockIds = allIngredients.filter { it.inStock }.mapNotNull { it.id }.toSet()
        
        val currentlyAvailable = getAvailableCocktailsWithSubstitutions()
            .filter { it.availability == CocktailAvailability.AVAILABLE }
            .map { it.cocktail.id }
            .toSet()
        
        val currentlyAvailableWithSubs = getAvailableCocktailsWithSubstitutions()
            .filter { it.availability in listOf(
                CocktailAvailability.AVAILABLE,
                CocktailAvailability.AVAILABLE_WITH_SUBSTITUTES,
                CocktailAvailability.AVAILABLE_WITH_ALTERNATIVES
            ) }
            .map { it.cocktail.id }
            .toSet()
        
        // Fetch cocktails once outside the loop
        val allCocktails = cocktailRepository.findAll()
        
        for (ingredient in outOfStockIngredients) {
            val ingredientId = ingredient.id ?: continue
            
            // Temporarily "add" this ingredient to stock for calculation
            val tempInStockIds = baseInStockIds + ingredientId
            
            var newDirectlyAvailable = 0
            var newAvailableAsAlternative = 0
            
            for (cocktail in allCocktails) {
                val cocktailId = cocktail.id ?: continue
                val requiredIds = cocktail.ingredients.map { it.ingredientId }.toSet()
                val missingIds = requiredIds - tempInStockIds
                
                if (missingIds.isEmpty()) {
                    // Would be directly available
                    if (cocktailId !in currentlyAvailable) {
                        newDirectlyAvailable++
                    }
                } else {
                    // Check if could be made with substitutes/alternatives
                    var canMake = true
                    var usesAlternative = false
                    
                    for (missingId in missingIds) {
                        val missingIngredient = ingredientMap[missingId]
                        if (missingIngredient != null) {
                            val hasSubstitute = missingIngredient.substituteIds.any { it in tempInStockIds }
                            val hasAlternative = missingIngredient.alternativeIds.any { it in tempInStockIds }
                            if (hasSubstitute) {
                                // OK - has substitute
                            } else if (hasAlternative) {
                                usesAlternative = true
                            } else {
                                canMake = false
                                break
                            }
                        } else {
                            canMake = false
                            break
                        }
                    }
                    
                    if (canMake && cocktailId !in currentlyAvailableWithSubs) {
                        if (usesAlternative) {
                            newAvailableAsAlternative++
                        } else {
                            newDirectlyAvailable++
                        }
                    }
                }
            }
            
            result[ingredientId] = Pair(newDirectlyAvailable, newAvailableAsAlternative)
        }
        
        return result
    }
    
    fun searchCocktails(name: String? = null, spirit: String? = null, tags: List<String>? = null): List<Cocktail> {
        val allCocktails = cocktailRepository.findAll()
        
        return allCocktails.filter { cocktail ->
            var matches = true
            
            // Filter by name (case-insensitive partial match)
            if (!name.isNullOrBlank()) {
                matches = matches && cocktail.name.contains(name, ignoreCase = true)
            }
            
            // Filter by spirit (check if any ingredient is the specified spirit)
            if (!spirit.isNullOrBlank()) {
                val hasSpirit = cocktail.ingredients.any { cocktailIng ->
                    val ingredient = ingredientRepository.findById(cocktailIng.ingredientId).orElse(null)
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
