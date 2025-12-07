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
        
        // Then set up relationships
        if (ingredientDTO.substituteIds.isNotEmpty()) {
            savedIngredient.substitutes = ingredientDTO.substituteIds
                .mapNotNull { ingredientRepository.findById(it).orElse(null) }
                .toMutableSet()
        }
        
        if (ingredientDTO.alternativeIds.isNotEmpty()) {
            savedIngredient.alternatives = ingredientDTO.alternativeIds
                .mapNotNull { ingredientRepository.findById(it).orElse(null) }
                .toMutableSet()
        }
        
        return ingredientRepository.save(savedIngredient)
    }

    fun deleteIngredient(id: Long) =
        ingredientRepository.deleteById(id)

    fun getInStockIngredients(): List<Ingredient> =
        ingredientRepository.findByInStock(true)


}