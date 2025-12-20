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
    private lateinit var mintLeaves: Ingredient
    
    @BeforeEach
    fun setup() {
        cocktailDataService = mockk()
        ingredientDataService = mockk()
        patchCocktailService = PatchCocktailService(cocktailDataService, ingredientDataService)
        
        vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        limeJuice = Ingredient(2, "Lime Juice", IngredientType.JUICE, 0, true)
        rum = Ingredient(3, "Rum", IngredientType.SPIRIT, 40, true)
        mintLeaves = Ingredient(4, "Mint Leaves", IngredientType.GARNISH, 0, true)
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
    
    @Test
    fun `calculateAbv should ignore non-volume ingredients with negative measures`() {
        // Given - Mojito with mint leaves (non-volume garnish)
        val ingredients = listOf(
            CocktailIngredient(3, 60.0),  // Rum 40% ABV
            CocktailIngredient(2, 30.0),  // Lime Juice 0% ABV
            CocktailIngredient(4, -8.0)   // 8 Mint Leaves (non-volume, should be ignored)
        )
        
        every { ingredientDataService.getIngredientById(3) } returns rum
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        every { ingredientDataService.getIngredientById(4) } returns mintLeaves
        
        // When
        val result = patchCocktailService.calculateAbv(ingredients)
        
        // Then - Should calculate based only on volume ingredients (60ml + 30ml)
        assertEquals(26, result) // (40*60 + 0*30) / 90 = 26.67 -> 26
    }
    
    @Test
    fun `calculateAbv should return 0 when only non-volume ingredients`() {
        // Given - Only garnishes (non-volume)
        val ingredients = listOf(
            CocktailIngredient(4, -8.0)  // 8 Mint Leaves
        )
        
        every { ingredientDataService.getIngredientById(4) } returns mintLeaves
        
        // When
        val result = patchCocktailService.calculateAbv(ingredients)
        
        // Then
        assertEquals(0, result)
    }
    
    @Test
    fun `determineBaseSpirit should ignore non-volume ingredients with negative measures`() {
        // Given - Mojito with mint leaves (non-volume garnish)
        val ingredients = listOf(
            CocktailIngredient(3, 60.0),  // Rum (highest volume spirit)
            CocktailIngredient(2, 30.0),  // Lime Juice
            CocktailIngredient(4, -8.0)   // 8 Mint Leaves (should be ignored)
        )
        
        every { ingredientDataService.getIngredientById(3) } returns rum
        every { ingredientDataService.getIngredientById(2) } returns limeJuice
        every { ingredientDataService.getIngredientById(4) } returns mintLeaves
        
        // When
        val result = patchCocktailService.determineBaseSpirit(ingredients)
        
        // Then
        assertEquals("Rum", result)
    }
    
    @Test
    fun `determineBaseSpirit should return none when only non-volume ingredients`() {
        // Given - Only garnishes (non-volume)
        val ingredients = listOf(
            CocktailIngredient(4, -8.0)  // 8 Mint Leaves
        )
        
        every { ingredientDataService.getIngredientById(4) } returns mintLeaves
        
        // When
        val result = patchCocktailService.determineBaseSpirit(ingredients)
        
        // Then
        assertEquals("none", result)
    }
    
    @Test
    fun `createCocktail should accept valid variation reference`() {
        // Given - Creating a variation of an existing cocktail
        val martini = Cocktail(
            id = 1,
            name = "Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka"
        )
        
        val dirtyMartini = Cocktail(
            name = "Dirty Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "",
            variationOfId = 1
        )
        
        every { cocktailDataService.getCocktailById(1) } returns martini
        every { ingredientDataService.getIngredientById(1) } returns vodka
        every { cocktailDataService.createCocktail(any()) } answers { firstArg() }
        
        // When
        val result = patchCocktailService.createCocktail(dirtyMartini)
        
        // Then
        assertNotNull(result)
        assertEquals(1L, result.variationOfId)
        verify { cocktailDataService.getCocktailById(1) }
    }
    
    @Test
    fun `createCocktail should reject reference to non-existent base cocktail`() {
        // Given
        val cocktail = Cocktail(
            name = "Test Cocktail",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "",
            variationOfId = 999
        )
        
        every { cocktailDataService.getCocktailById(999) } returns null
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            patchCocktailService.createCocktail(cocktail)
        }
        assertEquals("Base cocktail with ID 999 does not exist", exception.message)
    }
    
    @Test
    fun `updateCocktail should reject self-reference`() {
        // Given - Trying to make a cocktail a variation of itself
        val existing = Cocktail(
            id = 1,
            name = "Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka"
        )
        
        val update = Cocktail(
            id = 1,
            name = "Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka",
            variationOfId = 1
        )
        
        every { cocktailDataService.getCocktailById(1) } returns existing
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            patchCocktailService.updateCocktail(1, update)
        }
        assertEquals("A cocktail cannot be a variation of itself", exception.message)
    }
    
    @Test
    fun `updateCocktail should reject circular reference`() {
        // Given - A -> B -> C, trying to make C -> A (would create A -> B -> C -> A)
        val cocktailA = Cocktail(
            id = 1,
            name = "Cocktail A",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka",
            variationOfId = 3  // A is a variation of C
        )
        
        val cocktailB = Cocktail(
            id = 2,
            name = "Cocktail B",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka",
            variationOfId = 1  // B is a variation of A
        )
        
        val cocktailC = Cocktail(
            id = 3,
            name = "Cocktail C",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka",
            variationOfId = 2  // Trying to make C a variation of B (creates cycle)
        )
        
        val update = cocktailC.copy(variationOfId = 2)
        
        every { cocktailDataService.getCocktailById(3) } returns cocktailC
        every { cocktailDataService.getCocktailById(2) } returns cocktailB
        every { cocktailDataService.getCocktailById(1) } returns cocktailA
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            patchCocktailService.updateCocktail(3, update)
        }
        assertTrue(exception.message?.contains("Circular reference detected") ?: false)
    }
    
    @Test
    fun `updateCocktail should accept valid variation chain`() {
        // Given - Martini -> Dirty Martini -> Extra Dirty Martini
        val martini = Cocktail(
            id = 1,
            name = "Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka"
        )
        
        val dirtyMartini = Cocktail(
            id = 2,
            name = "Dirty Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka",
            variationOfId = 1
        )
        
        val extraDirtyMartini = Cocktail(
            id = 3,
            name = "Extra Dirty Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Stir"),
            tags = mutableListOf(),
            abv = 0,
            baseSpirit = "Vodka"
        )
        
        val update = extraDirtyMartini.copy(variationOfId = 2)
        
        every { cocktailDataService.getCocktailById(3) } returns extraDirtyMartini
        every { cocktailDataService.getCocktailById(2) } returns dirtyMartini
        every { cocktailDataService.getCocktailById(1) } returns martini
        every { ingredientDataService.getIngredientById(1) } returns vodka
        every { cocktailDataService.updateCocktail(3, any()) } answers { secondArg() }
        
        // When
        val result = patchCocktailService.updateCocktail(3, update)
        
        // Then
        assertNotNull(result)
        assertEquals(2L, result?.variationOfId)
        verify { cocktailDataService.updateCocktail(3, any()) }
    }
}
