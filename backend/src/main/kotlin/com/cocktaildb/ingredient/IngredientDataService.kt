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
}