package com.cocktaildb.service

import com.cocktaildb.model.Ingredient
import com.cocktaildb.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class IngredientService(
    private val ingredientRepository: IngredientRepository,
    private val stockUpdateService: StockUpdateService
) {
    
    fun getAllIngredients(): List<Ingredient> {
        return ingredientRepository.findAll()
    }
    
    fun getIngredientById(id: Long): Ingredient? {
        return ingredientRepository.findById(id).orElse(null)
    }
    
    fun createIngredient(ingredient: Ingredient): Ingredient {
        return ingredientRepository.save(ingredient)
    }
    
    fun updateIngredient(id: Long, ingredient: Ingredient): Ingredient? {
        val existing = ingredientRepository.findById(id).orElse(null) ?: return null
        val stockChanged = existing.inStock != ingredient.inStock
        existing.name = ingredient.name
        existing.type = ingredient.type
        existing.abv = ingredient.abv
        existing.inStock = ingredient.inStock
        existing.substituteIds = ingredient.substituteIds
        existing.alternativeIds = ingredient.alternativeIds
        val saved = ingredientRepository.save(existing)
        
        // Broadcast stock update if stock status changed
        if (stockChanged) {
            stockUpdateService.broadcastStockUpdate()
        }
        
        return saved
    }
    
    fun deleteIngredient(id: Long) {
        ingredientRepository.deleteById(id)
    }
    
    fun getInStockIngredients(): List<Ingredient> {
        return ingredientRepository.findByInStock(true)
    }
    
    /**
     * Get all ingredient IDs that can serve as a substitute or alternative for the given ingredient.
     * Returns a pair of (substituteIds, alternativeIds)
     */
    fun getEquivalentIngredientIds(ingredientId: Long): Pair<Set<Long>, Set<Long>> {
        val ingredient = ingredientRepository.findById(ingredientId).orElse(null) 
            ?: return Pair(emptySet(), emptySet())
        return Pair(ingredient.substituteIds.toSet(), ingredient.alternativeIds.toSet())
    }
    
    /**
     * Get all in-stock ingredient IDs including their in-stock substitutes.
     * Returns a map of original ingredient ID to set of in-stock substitute IDs.
     */
    fun getInStockWithSubstitutes(): Map<Long, Set<Long>> {
        val allIngredients = ingredientRepository.findAll()
        val inStockIds = allIngredients.filter { it.inStock }.mapNotNull { it.id }.toSet()
        
        val result = mutableMapOf<Long, Set<Long>>()
        for (ingredient in allIngredients) {
            val id = ingredient.id ?: continue
            val inStockSubstitutes = ingredient.substituteIds.filter { it in inStockIds }.toSet()
            if (inStockSubstitutes.isNotEmpty()) {
                result[id] = inStockSubstitutes
            }
        }
        return result
    }
    
    /**
     * Get all in-stock ingredient IDs including their in-stock alternatives.
     * Returns a map of original ingredient ID to set of in-stock alternative IDs.
     */
    fun getInStockWithAlternatives(): Map<Long, Set<Long>> {
        val allIngredients = ingredientRepository.findAll()
        val inStockIds = allIngredients.filter { it.inStock }.mapNotNull { it.id }.toSet()
        
        val result = mutableMapOf<Long, Set<Long>>()
        for (ingredient in allIngredients) {
            val id = ingredient.id ?: continue
            val inStockAlternatives = ingredient.alternativeIds.filter { it in inStockIds }.toSet()
            if (inStockAlternatives.isNotEmpty()) {
                result[id] = inStockAlternatives
            }
        }
        return result
    }
}
