package com.cocktaildb.ingredient

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class PatchIngredientServiceTest {
    
    private lateinit var ingredientDataService: IngredientDataService
    private lateinit var stockUpdateService: StockUpdateService
    private lateinit var patchIngredientService: PatchIngredientService
    
    @BeforeEach
    fun setup() {
        ingredientDataService = mockk()
        stockUpdateService = mockk(relaxed = true)
        patchIngredientService = PatchIngredientService(ingredientDataService, stockUpdateService)
    }
    
    @Test
    fun `updateIngredient should update ingredient properties`() {
        // Given
        val existing = Ingredient(
            id = 1,
            name = "Old Name",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        val update = Ingredient(
            name = "New Name",
            type = IngredientType.LIQUEUR,
            abv = 30,
            inStock = true
        )
        
        every { ingredientDataService.getIngredientById(1) } returns existing
        every { ingredientDataService.createIngredient(any()) } answers { firstArg() }
        
        // When
        val result = patchIngredientService.updateIngredient(1, update)
        
        // Then
        assertNotNull(result)
        assertEquals("New Name", result?.name)
        assertEquals(IngredientType.LIQUEUR, result?.type)
        assertEquals(30, result?.abv)
        assertTrue(result!!.inStock)
        verify { ingredientDataService.createIngredient(any()) }
    }
    
    @Test
    fun `updateIngredient should broadcast stock update when stock status changes from true to false`() {
        // Given
        val existing = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        val update = Ingredient(
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false
        )
        
        every { ingredientDataService.getIngredientById(1) } returns existing
        every { ingredientDataService.createIngredient(any()) } answers { firstArg() }
        
        // When
        patchIngredientService.updateIngredient(1, update)
        
        // Then
        verify(exactly = 1) { stockUpdateService.broadcastStockUpdate() }
    }
    
    @Test
    fun `updateIngredient should broadcast stock update when stock status changes from false to true`() {
        // Given
        val existing = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false
        )
        
        val update = Ingredient(
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        every { ingredientDataService.getIngredientById(1) } returns existing
        every { ingredientDataService.createIngredient(any()) } answers { firstArg() }
        
        // When
        patchIngredientService.updateIngredient(1, update)
        
        // Then
        verify(exactly = 1) { stockUpdateService.broadcastStockUpdate() }
    }
    
    @Test
    fun `updateIngredient should not broadcast stock update when stock status unchanged`() {
        // Given
        val existing = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        val update = Ingredient(
            name = "Vodka Updated",
            type = IngredientType.SPIRIT,
            abv = 45,
            inStock = true // Same stock status
        )
        
        every { ingredientDataService.getIngredientById(1) } returns existing
        every { ingredientDataService.createIngredient(any()) } answers { firstArg() }
        
        // When
        patchIngredientService.updateIngredient(1, update)
        
        // Then
        verify(exactly = 0) { stockUpdateService.broadcastStockUpdate() }
    }
    
    @Test
    fun `updateIngredient should return null when ingredient not exists`() {
        // Given
        val update = Ingredient(
            name = "New Name",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        every { ingredientDataService.getIngredientById(999) } returns null
        
        // When
        val result = patchIngredientService.updateIngredient(999, update)
        
        // Then
        assertNull(result)
        verify(exactly = 0) { ingredientDataService.createIngredient(any()) }
        verify(exactly = 0) { stockUpdateService.broadcastStockUpdate() }
    }
}
