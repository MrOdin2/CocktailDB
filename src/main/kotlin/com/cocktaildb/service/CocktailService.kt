package com.cocktaildb.service

import com.cocktaildb.model.Cocktail
import com.cocktaildb.repository.CocktailRepository
import com.cocktaildb.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class CocktailService(
    private val cocktailRepository: CocktailRepository,
    private val ingredientRepository: IngredientRepository
) {
    
    fun getAllCocktails(): List<Cocktail> {
        return cocktailRepository.findAll()
    }
    
    fun getCocktailById(id: Long): Cocktail? {
        return cocktailRepository.findById(id).orElse(null)
    }
    
    fun createCocktail(cocktail: Cocktail): Cocktail {
        return cocktailRepository.save(cocktail)
    }
    
    fun updateCocktail(id: Long, cocktail: Cocktail): Cocktail? {
        return if (cocktailRepository.existsById(id)) {
            cocktailRepository.save(cocktail.copy(id = id))
        } else {
            null
        }
    }
    
    fun deleteCocktail(id: Long) {
        cocktailRepository.deleteById(id)
    }
    
    fun getAvailableCocktails(): List<Cocktail> {
        val inStockIngredients = ingredientRepository.findByInStock(true)
        val inStockIngredientIds = inStockIngredients.mapNotNull { it.id }.toSet()
        
        val allCocktails = cocktailRepository.findAll()
        
        return allCocktails.filter { cocktail ->
            val requiredIngredientIds = cocktail.ingredients.map { it.ingredientId }.toSet()
            inStockIngredientIds.containsAll(requiredIngredientIds)
        }
    }
}
