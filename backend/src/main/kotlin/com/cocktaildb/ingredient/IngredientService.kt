package com.cocktaildb.ingredient

import com.cocktaildb.ingredient.StockUpdateService
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
}