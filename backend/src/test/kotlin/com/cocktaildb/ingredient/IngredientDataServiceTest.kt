package com.cocktaildb.ingredient

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

class IngredientDataServiceTest {
    
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var ingredientDataService: IngredientDataService
    
    @BeforeEach
    fun setup() {
        ingredientRepository = mockk()
        ingredientDataService = IngredientDataService(ingredientRepository)
    }
    
    @Test
    fun `getAllIngredients should return all ingredients`() {
        // Given
        val ingredients = listOf(
            createTestIngredient(1, "Vodka", true),
            createTestIngredient(2, "Rum", false)
        )
        every { ingredientRepository.findAll() } returns ingredients
        
        // When
        val result = ingredientDataService.getAllIngredients()
        
        // Then
        assertEquals(2, result.size)
        assertEquals("Vodka", result[0].name)
        assertEquals("Rum", result[1].name)
        verify { ingredientRepository.findAll() }
    }
    
    @Test
    fun `getIngredientById should return ingredient when exists`() {
        // Given
        val ingredient = createTestIngredient(1, "Vodka", true)
        every { ingredientRepository.findById(1) } returns Optional.of(ingredient)
        
        // When
        val result = ingredientDataService.getIngredientById(1)
        
        // Then
        assertNotNull(result)
        assertEquals("Vodka", result?.name)
        verify { ingredientRepository.findById(1) }
    }
    
    @Test
    fun `getIngredientById should return null when not exists`() {
        // Given
        every { ingredientRepository.findById(999) } returns Optional.empty()
        
        // When
        val result = ingredientDataService.getIngredientById(999)
        
        // Then
        assertNull(result)
        verify { ingredientRepository.findById(999) }
    }
    
    @Test
    fun `createIngredient should save and return ingredient`() {
        // Given
        val newIngredientDTO = IngredientDTO(
            id = null,
            name = "Gin",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        val savedIngredient = createTestIngredient(1, "Gin", true)
        every { ingredientRepository.save(any()) } returns savedIngredient
        
        // When
        val result = ingredientDataService.createIngredient(newIngredientDTO)
        
        // Then
        assertNotNull(result.id)
        assertEquals("Gin", result.name)
        verify { ingredientRepository.save(any()) }
    }
    
    @Test
    fun `deleteIngredient should call repository delete`() {
        // Given
        every { ingredientRepository.deleteById(1) } returns Unit
        
        // When
        ingredientDataService.deleteIngredient(1)
        
        // Then
        verify { ingredientRepository.deleteById(1) }
    }
    
    @Test
    fun `getInStockIngredients should return only in-stock ingredients`() {
        // Given
        val inStockIngredients = listOf(
            createTestIngredient(1, "Vodka", true),
            createTestIngredient(2, "Gin", true)
        )
        every { ingredientRepository.findByInStock(true) } returns inStockIngredients
        
        // When
        val result = ingredientDataService.getInStockIngredients()
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.inStock })
        verify { ingredientRepository.findByInStock(true) }
    }
    
    private fun createTestIngredient(id: Long?, name: String, inStock: Boolean): Ingredient {
        return Ingredient(
            id = id,
            name = name,
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = inStock
        )
    }
}
