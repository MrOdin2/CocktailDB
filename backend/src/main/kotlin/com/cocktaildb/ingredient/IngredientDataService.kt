package com.cocktaildb.ingredient

import org.springframework.stereotype.Service

@Service
class IngredientDataService(
    private val ingredientRepository: IngredientRepository,
) {

    fun getAllIngredients(): List<Ingredient> =
        ingredientRepository.findAll()

    fun getIngredientById(id: Long): Ingredient? =
        ingredientRepository.findById(id).orElse(null)

    fun createIngredient(ingredientDTO: IngredientDTO): Ingredient {
        val ingredient = Ingredient(
            name = ingredientDTO.name,
            type = ingredientDTO.type,
            abv = ingredientDTO.abv,
            inStock = ingredientDTO.inStock
        )
        
        // Save the ingredient first to get an ID
        val savedIngredient = ingredientRepository.save(ingredient)
        
        // Set up bidirectional relationships
        setupBidirectionalRelationships(savedIngredient, ingredientDTO.substituteIds, ingredientDTO.alternativeIds)
        
        return ingredientRepository.save(savedIngredient)
    }

    fun updateIngredient(ingredientDTO: IngredientDTO): Ingredient? {

        val existing = ingredientRepository.findById(ingredientDTO.id!!).orElse(null) ?: return null

        existing.name = ingredientDTO.name
        existing.type = ingredientDTO.type
        existing.abv = ingredientDTO.abv
        existing.inStock = ingredientDTO.inStock

        // Set up bidirectional relationships
        updateBidirectionalRelationships(existing, ingredientDTO.substituteIds, ingredientDTO.alternativeIds)

        return ingredientRepository.save(existing)
    }

    fun deleteIngredient(id: Long) {
        // Get the ingredient before deleting to clean up bidirectional relationships
        val ingredient = ingredientRepository.findById(id).orElse(null)
        if (ingredient != null) {
            // Remove this ingredient from all substitutes
            ingredient.substitutes.forEach { substitute ->
                substitute.substitutes.removeIf { it.id == id }
                ingredientRepository.save(substitute)
            }
            
            // Remove this ingredient from all alternatives
            ingredient.alternatives.forEach { alternative ->
                alternative.alternatives.removeIf { it.id == id }
                ingredientRepository.save(alternative)
            }
            
            // Now delete the ingredient
            ingredientRepository.deleteById(id)
        }
    }

    fun getInStockIngredients(): List<Ingredient> =
        ingredientRepository.findByInStock(true)

    /**
     * Sets up bidirectional substitute and alternative relationships.
     * When A is a substitute for B, B automatically becomes a substitute for A.
     */
    private fun setupBidirectionalRelationships(
        ingredient: Ingredient,
        substituteIds: Set<Long>,
        alternativeIds: Set<Long>
    ) {
        // Clear existing relationships for this ingredient
        ingredient.substitutes.clear()
        ingredient.alternatives.clear()
        
        // Set up substitutes bidirectionally
        if (substituteIds.isNotEmpty()) {
            val substitutes = ingredientRepository.findAllById(substituteIds).toList()
            substitutes.forEach { substitute ->
                // Add substitute to this ingredient
                ingredient.substitutes.add(substitute)
                // Add this ingredient to substitute (bidirectional)
                substitute.substitutes.add(ingredient)
                ingredientRepository.save(substitute)
            }
        }
        
        // Set up alternatives bidirectionally
        if (alternativeIds.isNotEmpty()) {
            val alternatives = ingredientRepository.findAllById(alternativeIds).toList()
            alternatives.forEach { alternative ->
                // Add alternative to this ingredient
                ingredient.alternatives.add(alternative)
                // Add this ingredient to alternative (bidirectional)
                alternative.alternatives.add(ingredient)
                ingredientRepository.save(alternative)
            }
        }
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
        val substitutesToUpdate = mutableListOf<Ingredient>()
        substitutesToRemove.forEach { removeId ->
            val toRemove = ingredientRepository.findById(removeId).orElse(null)
            if (toRemove != null) {
                ingredient.substitutes.removeIf { it.id == removeId }
                toRemove.substitutes.removeIf { it.id == ingredient.id }
                substitutesToUpdate.add(toRemove)
            }
        }
        ingredientRepository.saveAll(substitutesToUpdate)

        // Remove old alternative relationships bidirectionally
        val alternativesToUpdate = mutableListOf<Ingredient>()
        alternativesToRemove.forEach { removeId ->
            val toRemove = ingredientRepository.findById(removeId).orElse(null)
            if (toRemove != null) {
                ingredient.alternatives.removeIf { it.id == removeId }
                toRemove.alternatives.removeIf { it.id == ingredient.id }
                alternativesToUpdate.add(toRemove)
            }
        }
        ingredientRepository.saveAll(alternativesToUpdate)

        // Add new substitute relationships bidirectionally
        if (substitutesToAdd.isNotEmpty()) {
            val substitutes = ingredientRepository.findAllById(substitutesToAdd).toList()
            val substitutesToSave = mutableListOf<Ingredient>()
            substitutes.forEach { substitute ->
                ingredient.substitutes.add(substitute)
                substitute.substitutes.add(ingredient)
                substitutesToSave.add(substitute)
            }
            ingredientRepository.saveAll(substitutesToSave)
        }

        // Add new alternative relationships bidirectionally
        if (alternativesToAdd.isNotEmpty()) {
            val alternatives = ingredientRepository.findAllById(alternativesToAdd).toList()
            val alternativesToSave = mutableListOf<Ingredient>()
            alternatives.forEach { alternative ->
                ingredient.alternatives.add(alternative)
                alternative.alternatives.add(ingredient)
                alternativesToSave.add(alternative)
            }
            ingredientRepository.saveAll(alternativesToSave)
        }
    }
}