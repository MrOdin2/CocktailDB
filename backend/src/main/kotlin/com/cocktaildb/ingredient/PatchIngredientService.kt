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
        
        // Update bidirectional relationships
        updateBidirectionalRelationships(existing, ingredientDTO.substituteIds, ingredientDTO.alternativeIds)
        
        val saved = ingredientRepository.save(existing)

        // Broadcast stock update if stock status changed
        if (stockChanged) {
            stockUpdateService.broadcastStockUpdate()
        }

        return saved
    }

    /**
     * Updates bidirectional substitute and alternative relationships.
     * When A is a substitute for B, B automatically becomes a substitute for A.
     * Also removes old bidirectional relationships that are no longer needed.
     */
    private fun updateBidirectionalRelationships(
        ingredient: Ingredient,
        newSubstituteIds: Set<Long>,
        newAlternativeIds: Set<Long>
    ) {
        // Get current relationship IDs
        val currentSubstituteIds = ingredient.substitutes.mapNotNull { it.id }.toSet()
        val currentAlternativeIds = ingredient.alternatives.mapNotNull { it.id }.toSet()
        
        // Find IDs to remove (in current but not in new)
        val substitutesToRemove = currentSubstituteIds - newSubstituteIds
        val alternativesToRemove = currentAlternativeIds - newAlternativeIds
        
        // Find IDs to add (in new but not in current)
        val substitutesToAdd = newSubstituteIds - currentSubstituteIds
        val alternativesToAdd = newAlternativeIds - currentAlternativeIds
        
        // Remove old substitute relationships bidirectionally
        substitutesToRemove.forEach { removeId ->
            val toRemove = ingredientRepository.findById(removeId).orElse(null)
            if (toRemove != null) {
                ingredient.substitutes.removeIf { it.id == removeId }
                toRemove.substitutes.removeIf { it.id == ingredient.id }
                ingredientRepository.save(toRemove)
            }
        }
        
        // Remove old alternative relationships bidirectionally
        alternativesToRemove.forEach { removeId ->
            val toRemove = ingredientRepository.findById(removeId).orElse(null)
            if (toRemove != null) {
                ingredient.alternatives.removeIf { it.id == removeId }
                toRemove.alternatives.removeIf { it.id == ingredient.id }
                ingredientRepository.save(toRemove)
            }
        }
        
        // Add new substitute relationships bidirectionally
        if (substitutesToAdd.isNotEmpty()) {
            val substitutes = ingredientRepository.findAllById(substitutesToAdd).toList()
            substitutes.forEach { substitute ->
                ingredient.substitutes.add(substitute)
                substitute.substitutes.add(ingredient)
                ingredientRepository.save(substitute)
            }
        }
        
        // Add new alternative relationships bidirectionally
        if (alternativesToAdd.isNotEmpty()) {
            val alternatives = ingredientRepository.findAllById(alternativesToAdd).toList()
            alternatives.forEach { alternative ->
                ingredient.alternatives.add(alternative)
                alternative.alternatives.add(ingredient)
                ingredientRepository.save(alternative)
            }
        }
    }
}