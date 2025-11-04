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
        val existing = cocktailRepository.findById(id).orElse(null) ?: return null
        existing.name = cocktail.name
        existing.ingredients = cocktail.ingredients
        existing.steps = cocktail.steps
        existing.notes = cocktail.notes
        existing.tags = cocktail.tags
        return cocktailRepository.save(existing)
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
    
    fun searchCocktails(name: String? = null, spirit: String? = null, tags: List<String>? = null): List<Cocktail> {
        val allCocktails = cocktailRepository.findAll()
        
        return allCocktails.filter { cocktail ->
            var matches = true
            
            // Filter by name (case-insensitive partial match)
            if (!name.isNullOrBlank()) {
                matches = matches && cocktail.name.contains(name, ignoreCase = true)
            }
            
            // Filter by spirit (check if any ingredient is the specified spirit)
            if (!spirit.isNullOrBlank()) {
                val hasSpirit = cocktail.ingredients.any { cocktailIng ->
                    val ingredient = ingredientRepository.findById(cocktailIng.ingredientId).orElse(null)
                    ingredient != null && ingredient.name.equals(spirit, ignoreCase = true)
                }
                matches = matches && hasSpirit
            }
            
            // Filter by tags (cocktail must have all specified tags)
            if (!tags.isNullOrEmpty()) {
                val cocktailTags = cocktail.tags.map { it.lowercase() }
                matches = matches && tags.all { tag -> cocktailTags.contains(tag.lowercase()) }
            }
            
            matches
        }
    }
}
