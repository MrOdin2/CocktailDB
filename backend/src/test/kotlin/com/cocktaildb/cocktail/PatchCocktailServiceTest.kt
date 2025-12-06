package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.Ingredient
import com.cocktaildb.ingredient.IngredientDataService
import com.cocktaildb.ingredient.IngredientType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class PatchCocktailServiceTest {
    
    private lateinit var cocktailDataService: CocktailDataService
    private lateinit var ingredientDataService: IngredientDataService
    private lateinit var patchCocktailService: PatchCocktailService
    
    private lateinit var vodka: Ingredient
    private lateinit var limeJuice: Ingredient
    private lateinit var rum: Ingredient
    
    @BeforeEach
    fun setup() {
        cocktailDataService = mockk()
        ingredientDataService = mockk()
        patchCocktailService = PatchCocktailService(cocktailDataService, ingredientDataService)
        
        vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        limeJuice = Ingredient(2, "Lime Juice", IngredientType.JUICE, 0, true)
        rum = Ingredient(3, "Rum", IngredientType.SPIRIT, 40, true)
    }
    
    @Test
    fun `createCocktail should calculate ABV and base spirit`() {
        // Given
        val cocktail = Cocktail(
            name = "Vodka Lime",
            ingredients = mutableListOf(
                CocktailIngredient(1, 60.0),
                CocktailIngredient(2, 30.0)
            ),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = ""
        )
        
        every { ingredientDataService.getIngredientById(1) } returns vodka
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        every { cocktailDataService.createCocktail(any()) } answers { firstArg() }
        
        // When
        val result = patchCocktailService.createCocktail(cocktail)
        
        // Then
        assertEquals(26, result.abv) // (40*60 + 0*30) / 90 = 26.67 -> 26
        assertEquals("Vodka", result.baseSpirit)
        verify { cocktailDataService.createCocktail(any()) }
    }
    
    @Test
    fun `updateCocktail should recalculate ABV and base spirit`() {
        // Given
        val existing = Cocktail(
            id = 1,
            name = "Old Name",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Old step"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        val update = Cocktail(
            name = "New Name",
            ingredients = mutableListOf(
                CocktailIngredient(3, 60.0),
                CocktailIngredient(2, 30.0)
            ),
            steps = mutableListOf("New step"),
            notes = "New notes",
            tags = mutableListOf("updated"),
            abv = 0,
            baseSpirit = ""
        )
        
        every { cocktailDataService.getCocktailById(1) } returns existing
        every { ingredientDataService.getIngredientById(3) } returns rum
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        every { cocktailDataService.updateCocktail(1, any()) } answers { secondArg() }
        
        // When
        val result = patchCocktailService.updateCocktail(1, update)
        
        // Then
        assertNotNull(result)
        assertEquals("New Name", result?.name)
        assertEquals(26, result?.abv) // (40*60 + 0*30) / 90 = 26.67 -> 26
        assertEquals("Rum", result?.baseSpirit)
        verify { cocktailDataService.updateCocktail(1, any()) }
    }
    
    @Test
    fun `updateCocktail should return null when cocktail does not exist`() {
        // Given
        val update = Cocktail(
            name = "New Name",
            ingredients = mutableListOf(),
            steps = mutableListOf(),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = ""
        )
        
        every { cocktailDataService.getCocktailById(999) } returns null
        
        // When
        val result = patchCocktailService.updateCocktail(999, update)
        
        // Then
        assertNull(result)
        verify(exactly = 0) { cocktailDataService.updateCocktail(any(), any()) }
    }
    
    @Test
    fun `calculateAbv should return weighted average`() {
        // Given
        val ingredients = listOf(
            CocktailIngredient(1, 60.0), // Vodka 40%
            CocktailIngredient(2, 30.0)  // Lime Juice 0%
        )
        
        every { ingredientDataService.getIngredientById(1) } returns vodka
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        
        // When
        val result = patchCocktailService.calculateAbv(ingredients)
        
        // Then
        assertEquals(26, result) // (40*60 + 0*30) / 90 = 26.67 -> 26
    }
    
    @Test
    fun `calculateAbv should return 0 for empty ingredients`() {
        // When
        val result = patchCocktailService.calculateAbv(emptyList())
        
        // Then
        assertEquals(0, result)
    }
    
    @Test
    fun `calculateAbv should return 0 when total volume is 0`() {
        // Given
        val ingredients = listOf(CocktailIngredient(1, 0.0))
        
        // When
        val result = patchCocktailService.calculateAbv(ingredients)
        
        // Then
        assertEquals(0, result)
    }
    
    @Test
    fun `calculateAbv should handle null ingredients`() {
        // Given
        val ingredients = listOf(CocktailIngredient(999, 60.0))
        
        every { ingredientDataService.getIngredientById(999) } returns null
        
        // When
        val result = patchCocktailService.calculateAbv(ingredients)
        
        // Then
        assertEquals(0, result)
    }
    
    @Test
    fun `determineBaseSpirit should return spirit with highest volume`() {
        // Given
        val ingredients = listOf(
            CocktailIngredient(1, 30.0), // Vodka
            CocktailIngredient(3, 60.0), // Rum (highest)
            CocktailIngredient(2, 20.0)  // Lime Juice (non-spirit)
        )
        
        every { ingredientDataService.getIngredientById(1) } returns vodka
        every { ingredientDataService.getIngredientById(3) } returns rum
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        
        // When
        val result = patchCocktailService.determineBaseSpirit(ingredients)
        
        // Then
        assertEquals("Rum", result)
    }
    
    @Test
    fun `determineBaseSpirit should return Unknown for empty ingredients`() {
        // When
        val result = patchCocktailService.determineBaseSpirit(emptyList())
        
        // Then
        assertEquals("Unknown", result)
    }
    
    @Test
    fun `determineBaseSpirit should return none when no spirits`() {
        // Given
        val ingredients = listOf(CocktailIngredient(2, 100.0))
        
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        
        // When
        val result = patchCocktailService.determineBaseSpirit(ingredients)
        
        // Then
        assertEquals("none", result)
    }
    
    @Test
    fun `determineBaseSpirit should return none when all ingredients are null`() {
        // Given
        val ingredients = listOf(CocktailIngredient(999, 60.0))
        
        every { ingredientDataService.getIngredientById(999) } returns null
        
        // When
        val result = patchCocktailService.determineBaseSpirit(ingredients)
        
        // Then
        assertEquals("none", result)
    }
}
