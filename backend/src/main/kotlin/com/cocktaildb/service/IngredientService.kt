package com.cocktaildb.service

import com.cocktaildb.model.Ingredient
import com.cocktaildb.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class IngredientService(
    private val ingredientRepository: IngredientRepository
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
        existing.name = ingredient.name
        existing.type = ingredient.type
        existing.abv = ingredient.abv
        existing.inStock = ingredient.inStock
        return ingredientRepository.save(existing)
    }
    
    fun deleteIngredient(id: Long) {
        ingredientRepository.deleteById(id)
    }
    
    fun getInStockIngredients(): List<Ingredient> {
        return ingredientRepository.findByInStock(true)
    }
}
