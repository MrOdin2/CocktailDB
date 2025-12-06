package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.Ingredient
import com.cocktaildb.ingredient.IngredientDataService
import com.cocktaildb.ingredient.IngredientType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class CocktailSearchServiceTest {
    
    private lateinit var cocktailDataService: CocktailDataService
    private lateinit var ingredientDataService: IngredientDataService
    private lateinit var cocktailSearchService: CocktailSearchService
    
    @BeforeEach
    fun setup() {
        cocktailDataService = mockk()
        ingredientDataService = mockk()
        cocktailSearchService = CocktailSearchService(cocktailDataService, ingredientDataService)
    }
    
    @Test
    fun `getAvailableCocktails should return cocktails with all ingredients in stock`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        val lime = Ingredient(2, "Lime Juice", IngredientType.JUICE, 0, true)
        val rum = Ingredient(3, "Rum", IngredientType.SPIRIT, 40, false) // Out of stock
        
        val mojito = Cocktail(
            id = 1,
            name = "Mojito",
            ingredients = mutableListOf(CocktailIngredient(3, 60.0), CocktailIngredient(2, 30.0)),
            steps = mutableListOf(),
            tags = mutableListOf(),
            abv = 15,
            baseSpirit = "Rum"
        )
        
        val gimlet = Cocktail(
            id = 2,
            name = "Gimlet",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0), CocktailIngredient(2, 30.0)),
            steps = mutableListOf(),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(vodka, lime)
        every { cocktailDataService.getAll() } returns listOf(mojito, gimlet)
        
        // When
        val result = cocktailSearchService.getAvailableCocktails()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Gimlet", result[0].name) // Only Gimlet has all ingredients in stock
    }
    
    @Test
    fun `getAvailableCocktails should return empty list when no cocktails have all ingredients`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        
        val mojito = Cocktail(
            id = 1,
            name = "Mojito",
            ingredients = mutableListOf(CocktailIngredient(3, 60.0)), // Rum - not in stock
            steps = mutableListOf(),
            tags = mutableListOf(),
            abv = 15,
            baseSpirit = "Rum"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(vodka)
        every { cocktailDataService.getAll() } returns listOf(mojito)
        
        // When
        val result = cocktailSearchService.getAvailableCocktails()
        
        // Then
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `searchCocktails should filter by name case-insensitive`() {
        // Given
        val cocktails = listOf(
            createTestCocktail(1, "Mojito"),
            createTestCocktail(2, "Martini"),
            createTestCocktail(3, "Manhattan")
        )
        every { cocktailDataService.getAll() } returns cocktails
        
        // When
        val result = cocktailSearchService.searchCocktails(name = "mart")
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Martini", result[0].name)
    }
    
    @Test
    fun `searchCocktails should filter by spirit`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        val rum = Ingredient(2, "Rum", IngredientType.SPIRIT, 40, true)
        
        val vodkaCocktail = Cocktail(
            id = 1,
            name = "Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf(),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        val rumCocktail = Cocktail(
            id = 2,
            name = "Mojito",
            ingredients = mutableListOf(CocktailIngredient(2, 60.0)),
            steps = mutableListOf(),
            tags = mutableListOf(),
            abv = 15,
            baseSpirit = "Rum"
        )
        
        every { cocktailDataService.getAll() } returns listOf(vodkaCocktail, rumCocktail)
        every { ingredientDataService.getIngredientById(1) } returns vodka
        every { ingredientDataService.getIngredientById(2) } returns rum
        
        // When
        val result = cocktailSearchService.searchCocktails(spirit = "vodka")
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Martini", result[0].name)
    }
    
    @Test
    fun `searchCocktails should filter by tags`() {
        // Given
        val cocktails = listOf(
            createTestCocktail(1, "Mojito", tags = mutableListOf("refreshing", "summer")),
            createTestCocktail(2, "Martini", tags = mutableListOf("classic", "strong")),
            createTestCocktail(3, "Daiquiri", tags = mutableListOf("refreshing", "classic"))
        )
        every { cocktailDataService.getAll() } returns cocktails
        
        // When
        val result = cocktailSearchService.searchCocktails(tags = listOf("refreshing"))
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Mojito" })
        assertTrue(result.any { it.name == "Daiquiri" })
    }
    
    @Test
    fun `searchCocktails should filter by multiple tags requiring all`() {
        // Given
        val cocktails = listOf(
            createTestCocktail(1, "Mojito", tags = mutableListOf("refreshing", "summer")),
            createTestCocktail(2, "Martini", tags = mutableListOf("classic", "strong")),
            createTestCocktail(3, "Daiquiri", tags = mutableListOf("refreshing", "classic"))
        )
        every { cocktailDataService.getAll() } returns cocktails
        
        // When
        val result = cocktailSearchService.searchCocktails(tags = listOf("refreshing", "classic"))
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Daiquiri", result[0].name)
    }
    
    @Test
    fun `searchCocktails should combine multiple filters`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        
        val cocktail1 = Cocktail(
            id = 1,
            name = "Vodka Martini",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf(),
            tags = mutableListOf("classic"),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        val cocktail2 = Cocktail(
            id = 2,
            name = "Vodka Soda",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf(),
            tags = mutableListOf("simple"),
            abv = 15,
            baseSpirit = "Vodka"
        )
        
        every { cocktailDataService.getAll() } returns listOf(cocktail1, cocktail2)
        every { ingredientDataService.getIngredientById(1) } returns vodka
        
        // When
        val result = cocktailSearchService.searchCocktails(
            name = "martini",
            spirit = "vodka",
            tags = listOf("classic")
        )
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Vodka Martini", result[0].name)
    }
    
    private fun createTestCocktail(
        id: Long?,
        name: String,
        tags: MutableList<String> = mutableListOf()
    ): Cocktail {
        return Cocktail(
            id = id,
            name = name,
            ingredients = mutableListOf(),
            steps = mutableListOf("Step 1"),
            notes = "Test notes",
            tags = tags,
            abv = 15,
            baseSpirit = "Vodka"
        )
    }
}
