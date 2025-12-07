package com.cocktaildb.ingredient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class IngredientRepositoryTest {
    
    @Autowired
    private lateinit var ingredientRepository: IngredientRepository
    
    @BeforeEach
    fun setup() {
        ingredientRepository.deleteAll()
    }
    
    @Test
    fun `should save and retrieve ingredient`() {
        // Given
        val ingredient = Ingredient(
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        // When
        val saved = ingredientRepository.save(ingredient)
        val retrieved = ingredientRepository.findById(saved.id!!).orElse(null)
        
        // Then
        assertNotNull(retrieved)
        assertEquals("Vodka", retrieved.name)
        assertEquals(IngredientType.SPIRIT, retrieved.type)
        assertEquals(40, retrieved.abv)
        assertTrue(retrieved.inStock)
    }
    
    @Test
    fun `findByInStock should return only in-stock ingredients`() {
        // Given
        ingredientRepository.save(
            Ingredient(name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        )
        ingredientRepository.save(
            Ingredient(name = "Rum", type = IngredientType.SPIRIT, abv = 40, inStock = false)
        )
        ingredientRepository.save(
            Ingredient(name = "Gin", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        )
        
        // When
        val inStock = ingredientRepository.findByInStock(true)
        
        // Then
        assertEquals(2, inStock.size)
        assertTrue(inStock.all { it.inStock })
        assertTrue(inStock.any { it.name == "Vodka" })
        assertTrue(inStock.any { it.name == "Gin" })
    }
    
    @Test
    fun `findByInStock should return empty list when no ingredients match`() {
        // Given
        ingredientRepository.save(
            Ingredient(name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = false)
        )
        
        // When
        val inStock = ingredientRepository.findByInStock(true)
        
        // Then
        assertTrue(inStock.isEmpty())
    }
    
    @Test
    fun `should update ingredient`() {
        // Given
        val ingredient = ingredientRepository.save(
            Ingredient(name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        )
        
        // When
        ingredient.inStock = false
        ingredient.abv = 45
        ingredientRepository.save(ingredient)
        
        val updated = ingredientRepository.findById(ingredient.id!!).orElse(null)
        
        // Then
        assertNotNull(updated)
        assertFalse(updated.inStock)
        assertEquals(45, updated.abv)
    }
    
    @Test
    fun `should delete ingredient`() {
        // Given
        val ingredient = ingredientRepository.save(
            Ingredient(name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        )
        
        // When
        ingredientRepository.deleteById(ingredient.id!!)
        
        // Then
        assertFalse(ingredientRepository.existsById(ingredient.id!!))
    }
}
