package com.cocktaildb.ingredient

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

class PatchIngredientServiceTest {
    
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var stockUpdateService: StockUpdateService
    private lateinit var patchIngredientService: PatchIngredientService
    
    @BeforeEach
    fun setup() {
        ingredientRepository = mockk()
        stockUpdateService = mockk(relaxed = true)
        patchIngredientService = PatchIngredientService(ingredientRepository, stockUpdateService)
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
        
        val updateDTO = IngredientDTO(
            id = 1,
            name = "New Name",
            type = IngredientType.LIQUEUR,
            abv = 30,
            inStock = true
        )
        
        every { ingredientRepository.findById(1) } returns Optional.of(existing)
        every { ingredientRepository.save(any()) } answers { firstArg() }
        
        // When
        val result = patchIngredientService.updateIngredient(1, updateDTO)
        
        // Then
        assertNotNull(result)
        assertEquals("New Name", result?.name)
        assertEquals(IngredientType.LIQUEUR, result?.type)
        assertEquals(30, result?.abv)
        assertTrue(result!!.inStock)
        verify { ingredientRepository.save(any()) }
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
        
        val updateDTO = IngredientDTO(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false
        )
        
        every { ingredientRepository.findById(1) } returns Optional.of(existing)
        every { ingredientRepository.save(any()) } answers { firstArg() }
        
        // When
        patchIngredientService.updateIngredient(1, updateDTO)
        
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
        
        val updateDTO = IngredientDTO(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        every { ingredientRepository.findById(1) } returns Optional.of(existing)
        every { ingredientRepository.save(any()) } answers { firstArg() }
        
        // When
        patchIngredientService.updateIngredient(1, updateDTO)
        
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
        
        val updateDTO = IngredientDTO(
            id = 1,
            name = "Vodka Updated",
            type = IngredientType.SPIRIT,
            abv = 45,
            inStock = true // Same stock status
        )
        
        every { ingredientRepository.findById(1) } returns Optional.of(existing)
        every { ingredientRepository.save(any()) } answers { firstArg() }
        
        // When
        patchIngredientService.updateIngredient(1, updateDTO)
        
        // Then
        verify(exactly = 0) { stockUpdateService.broadcastStockUpdate() }
    }
    
    @Test
    fun `updateIngredient should return null when ingredient does not exist`() {
        // Given
        val updateDTO = IngredientDTO(
            id = 999,
            name = "New Name",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        
        every { ingredientRepository.findById(999) } returns Optional.empty()
        
        // When
        val result = patchIngredientService.updateIngredient(999, updateDTO)
        
        // Then
        assertNull(result)
        verify(exactly = 0) { ingredientRepository.save(any()) }
        verify(exactly = 0) { stockUpdateService.broadcastStockUpdate() }
    }
}
