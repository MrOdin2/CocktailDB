package com.cocktaildb.ingredient

import org.springframework.stereotype.Service

@Service
class PatchIngredientService(
    private val ingredientDataService: IngredientDataService,
    private val stockUpdateService: StockUpdateService,
) {

    fun updateIngredient(id: Long, ingredient: Ingredient): Ingredient? {
        val existing = ingredientDataService.getIngredientById(id) ?: return null
        val stockChanged = existing.inStock != ingredient.inStock
        existing.name = ingredient.name
        existing.type = ingredient.type
        existing.abv = ingredient.abv
        existing.inStock = ingredient.inStock
        val saved = ingredientDataService.createIngredient(existing)

        // Broadcast stock update if stock status changed
        if (stockChanged) {
            stockUpdateService.broadcastStockUpdate()
        }

        return saved
    }
}