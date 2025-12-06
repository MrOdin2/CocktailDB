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

    fun createIngredient(ingredient: Ingredient): Ingredient =
        ingredientRepository.save(ingredient)

    fun deleteIngredient(id: Long) =
        ingredientRepository.deleteById(id)

    fun getInStockIngredients(): List<Ingredient> =
        ingredientRepository.findByInStock(true)


}