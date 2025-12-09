package com.cocktaildb.ingredient

import org.springframework.stereotype.Service

@Service
class PatchIngredientService(
    private val ingredientDataService: IngredientDataService,
    private val stockUpdateService: StockUpdateService,
) {

    fun updateIngredient(ingredientDTO: IngredientDTO): Ingredient? {
        val updated = ingredientDataService.updateIngredient(ingredientDTO) ?: return null

        // Broadcast stock update
        stockUpdateService.broadcastStockUpdate()

        return updated
    }

}