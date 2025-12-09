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
    
    @BeforeEach
    fun setup() {
        stockUpdateService = mockk(relaxed = true)
        ingredientDataService = mockk(relaxed = true)
        patchIngredientService = PatchIngredientService(ingredientDataService, stockUpdateService)
    }
    
    @Test
    fun `updateIngredient should update ingredient properties`() {

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
    fun `updateIngredient should broadcast stock update`() {

        val updateDTO = IngredientDTO(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false
        )

        val updatedIngredient = Ingredient(
            id = 1,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = false
        )

        every { ingredientDataService.updateIngredient(updateDTO) } returns updatedIngredient

        // When
        patchIngredientService.updateIngredient(updateDTO)
        
        // Then
        verify(exactly = 1) { stockUpdateService.broadcastStockUpdate() }
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
        
        every { ingredientDataService.updateIngredient(updateDTO) } returns null

        // When
        val result = patchIngredientService.updateIngredient(updateDTO)
        
        // Then
        assertNull(result)
        verify(exactly = 1) { ingredientDataService.updateIngredient(any()) }
        verify(exactly = 0) { stockUpdateService.broadcastStockUpdate() }
    }
}
