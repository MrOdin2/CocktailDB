package com.cocktaildb.ingredient

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class PatchIngredientServiceTest {

    private lateinit var stockUpdateService: StockUpdateService
    private lateinit var patchIngredientService: PatchIngredientService
    private lateinit var ingredientDataService: IngredientDataService
    private lateinit var ingredientRepository: IngredientRepository
    
    @BeforeEach
    fun setup() {
        stockUpdateService = mockk(relaxed = true)
        ingredientDataService = mockk(relaxed = true)
        ingredientRepository = mockk(relaxed = true)
        patchIngredientService = PatchIngredientService(ingredientDataService, ingredientRepository, stockUpdateService)
    }
    
    @Test
    fun `updateIngredient should update ingredient properties`() {

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

        val updated = Ingredient(
            id = 1,
            name = "New Name",
            type = IngredientType.LIQUEUR,
            abv = 30,
            inStock = true
        )

        every { ingredientRepository.findById(1) } returns java.util.Optional.of(existing)
        every { ingredientDataService.updateIngredient(any()) } answers { updated }

        // When
        val result = patchIngredientService.updateIngredient(updateDTO)
        
        // Then
        assertNotNull(result)
        assertEquals("New Name", result?.name)
        assertEquals(IngredientType.LIQUEUR, result?.type)
        assertEquals(30, result?.abv)
        assertTrue(result!!.inStock)
        verify { ingredientDataService.updateIngredient(any()) }
    }
    
    @Test
    fun `updateIngredient should broadcast stock update when stock changes`() {

        val existing = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true  // Was in stock
        )

        val updateDTO = IngredientDTO(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false  // Now out of stock
        )

        val updatedIngredient = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false
        )

        every { ingredientRepository.findById(1) } returns java.util.Optional.of(existing)
        every { ingredientDataService.updateIngredient(updateDTO) } returns updatedIngredient

        // When
        patchIngredientService.updateIngredient(updateDTO)
        
        // Then
        verify(exactly = 1) { stockUpdateService.broadcastStockUpdate() }
    }

    @Test
    fun `updateIngredient should not broadcast when stock unchanged`() {

        val existing = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )

        val updateDTO = IngredientDTO(
            id = 1,
            name = "Vodka Updated",  // Only name changed
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true  // Stock unchanged
        )

        val updatedIngredient = Ingredient(
            id = 1,
            name = "Vodka Updated",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )

        every { ingredientRepository.findById(1) } returns java.util.Optional.of(existing)
        every { ingredientDataService.updateIngredient(updateDTO) } returns updatedIngredient

        // When
        patchIngredientService.updateIngredient(updateDTO)
        
        // Then - should not broadcast when stock unchanged
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
        
        every { ingredientRepository.findById(999) } returns java.util.Optional.empty()

        // When
        val result = patchIngredientService.updateIngredient(updateDTO)
        
        // Then
        assertNull(result)
        verify(exactly = 0) { ingredientDataService.updateIngredient(any()) }
        verify(exactly = 0) { stockUpdateService.broadcastStockUpdate() }
    }
}
