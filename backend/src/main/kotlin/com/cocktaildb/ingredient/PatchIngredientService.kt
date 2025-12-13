package com.cocktaildb.ingredient

import org.springframework.stereotype.Service

@Service
class PatchIngredientService(
    private val ingredientDataService: IngredientDataService,
    private val ingredientRepository: IngredientRepository,
    private val stockUpdateService: StockUpdateService,
) {

    fun updateIngredient(ingredientDTO: IngredientDTO): Ingredient? {
        // Get the existing ingredient to compare stock status
        val existing = ingredientRepository.findById(ingredientDTO.id!!).orElse(null) ?: return null
        val stockChanged = existing.inStock != ingredientDTO.inStock
        
        val updated = ingredientDataService.updateIngredient(ingredientDTO) ?: return null

        // Broadcast stock update only if stock status changed
        if (stockChanged) {
            stockUpdateService.broadcastStockUpdate()
        }

        return updated
    }

}