package com.cocktaildb.ingredient

import org.springframework.stereotype.Service

@Service
class PatchIngredientService(
    private val ingredientRepository: IngredientRepository,
    private val stockUpdateService: StockUpdateService,
) {

    fun updateIngredient(id: Long, ingredientDTO: IngredientDTO): Ingredient? {
        val existing = ingredientRepository.findById(id).orElse(null) ?: return null
        val stockChanged = existing.inStock != ingredientDTO.inStock
        
        existing.name = ingredientDTO.name
        existing.type = ingredientDTO.type
        existing.abv = ingredientDTO.abv
        existing.inStock = ingredientDTO.inStock
        
        // Update substitutes
        existing.substitutes.clear()
        if (ingredientDTO.substituteIds.isNotEmpty()) {
            existing.substitutes.addAll(
                ingredientDTO.substituteIds
                    .mapNotNull { ingredientRepository.findById(it).orElse(null) }
            )
        }
        
        // Update alternatives
        existing.alternatives.clear()
        if (ingredientDTO.alternativeIds.isNotEmpty()) {
            existing.alternatives.addAll(
                ingredientDTO.alternativeIds
                    .mapNotNull { ingredientRepository.findById(it).orElse(null) }
            )
        }
        
        val saved = ingredientRepository.save(existing)

        // Broadcast stock update if stock status changed
        if (stockChanged) {
            stockUpdateService.broadcastStockUpdate()
        }

        return saved
    }
}