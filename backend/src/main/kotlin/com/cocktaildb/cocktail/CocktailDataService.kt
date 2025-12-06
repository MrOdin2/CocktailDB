package com.cocktaildb.cocktail

import org.springframework.stereotype.Service

@Service
class CocktailDataService(
    private val cocktailRepository: CocktailRepository,
) {
    fun getAllCocktails(): List<Cocktail> =
        cocktailRepository.findAll()

    fun getCocktailById(id: Long): Cocktail? =
        cocktailRepository.findById(id).orElse(null)

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
        existing.abv = cocktail.abv
        existing.baseSpirit = cocktail.baseSpirit
        return cocktailRepository.save(existing)
    }

    fun deleteCocktail(id: Long): Boolean {
        val exists = cocktailRepository.existsById(id)
        cocktailRepository.deleteById(id)
        return exists
    }

    fun getAll() = cocktailRepository.findAll()

}