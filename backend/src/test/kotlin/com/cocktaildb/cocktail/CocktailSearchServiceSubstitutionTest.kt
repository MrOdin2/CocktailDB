package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.Ingredient
import com.cocktaildb.ingredient.IngredientDataService
import com.cocktaildb.ingredient.IngredientType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class CocktailSearchServiceSubstitutionTest {
    
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
    fun `getAvailableCocktailsWithSubstitutions should categorize cocktails with exact ingredients`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        val lime = Ingredient(2, "Lime Juice", IngredientType.JUICE, 0, true)
        
        val gimlet = Cocktail(
            id = 1,
            name = "Gimlet",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0), CocktailIngredient(2, 30.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(vodka, lime)
        every { cocktailDataService.getAll() } returns listOf(gimlet)
        
        // When
        val result = cocktailSearchService.getAvailableCocktailsWithSubstitutions()
        
        // Then
        assertEquals(1, result["exact"]?.size)
        assertEquals("Gimlet", result["exact"]?.get(0)?.name)
        assertEquals(0, result["withSubstitutes"]?.size)
        assertEquals(0, result["withAlternatives"]?.size)
    }
    
    @Test
    fun `getAvailableCocktailsWithSubstitutions should categorize cocktails available with substitutes`() {
        // Given
        val malibu = Ingredient(1, "Malibu", IngredientType.LIQUEUR, 21, true)
        val coconutRum = Ingredient(2, "Coconut Rum", IngredientType.SPIRIT, 40, false)
        val pineapple = Ingredient(3, "Pineapple Juice", IngredientType.JUICE, 0, true)
        
        // Set up substitute relationship
        malibu.substitutes.add(coconutRum)
        
        val pinaColada = Cocktail(
            id = 1,
            name = "Pina Colada",
            ingredients = mutableListOf(CocktailIngredient(2, 60.0), CocktailIngredient(3, 90.0)),
            steps = mutableListOf("Blend"),
            tags = mutableListOf(),
            abv = 15,
            baseSpirit = "Rum"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(malibu, pineapple)
        every { cocktailDataService.getAll() } returns listOf(pinaColada)
        
        // When
        val result = cocktailSearchService.getAvailableCocktailsWithSubstitutions()
        
        // Then
        assertEquals(0, result["exact"]?.size)
        assertEquals(1, result["withSubstitutes"]?.size)
        assertEquals("Pina Colada", result["withSubstitutes"]?.get(0)?.name)
        assertEquals(0, result["withAlternatives"]?.size)
    }
    
    @Test
    fun `getAvailableCocktailsWithSubstitutions should categorize cocktails available with alternatives`() {
        // Given
        val champagne = Ingredient(1, "Champagne", IngredientType.WINE, 12, true)
        val prosecco = Ingredient(2, "Prosecco", IngredientType.WINE, 11, false)
        val orange = Ingredient(3, "Orange Juice", IngredientType.JUICE, 0, true)
        
        // Set up alternative relationship
        champagne.alternatives.add(prosecco)
        
        val mimosa = Cocktail(
            id = 1,
            name = "Mimosa",
            ingredients = mutableListOf(CocktailIngredient(2, 90.0), CocktailIngredient(3, 90.0)),
            steps = mutableListOf("Mix gently"),
            tags = mutableListOf(),
            abv = 6,
            baseSpirit = "Wine"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(champagne, orange)
        every { cocktailDataService.getAll() } returns listOf(mimosa)
        
        // When
        val result = cocktailSearchService.getAvailableCocktailsWithSubstitutions()
        
        // Then
        assertEquals(0, result["exact"]?.size)
        assertEquals(0, result["withSubstitutes"]?.size)
        assertEquals(1, result["withAlternatives"]?.size)
        assertEquals("Mimosa", result["withAlternatives"]?.get(0)?.name)
    }
    
    @Test
    fun `getAvailableCocktailsWithSubstitutions should handle multiple cocktails in different categories`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        val vanillaVodka = Ingredient(2, "Vanilla Vodka", IngredientType.SPIRIT, 35, false)
        val champagne = Ingredient(3, "Champagne", IngredientType.WINE, 12, true)
        val prosecco = Ingredient(4, "Prosecco", IngredientType.WINE, 11, false)
        val lime = Ingredient(5, "Lime Juice", IngredientType.JUICE, 0, true)
        val orange = Ingredient(6, "Orange Juice", IngredientType.JUICE, 0, true)
        
        vodka.substitutes.add(vanillaVodka)
        champagne.alternatives.add(prosecco)
        
        val gimlet = Cocktail(
            id = 1,
            name = "Gimlet",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0), CocktailIngredient(5, 30.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        val vanillaLime = Cocktail(
            id = 2,
            name = "Vanilla Lime",
            ingredients = mutableListOf(CocktailIngredient(2, 60.0), CocktailIngredient(5, 30.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 18,
            baseSpirit = "Vodka"
        )
        
        val mimosa = Cocktail(
            id = 3,
            name = "Mimosa",
            ingredients = mutableListOf(CocktailIngredient(4, 90.0), CocktailIngredient(6, 90.0)),
            steps = mutableListOf("Mix gently"),
            tags = mutableListOf(),
            abv = 6,
            baseSpirit = "Wine"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(vodka, champagne, lime, orange)
        every { cocktailDataService.getAll() } returns listOf(gimlet, vanillaLime, mimosa)
        
        // When
        val result = cocktailSearchService.getAvailableCocktailsWithSubstitutions()
        
        // Then
        assertEquals(1, result["exact"]?.size)
        assertEquals("Gimlet", result["exact"]?.get(0)?.name)
        
        assertEquals(1, result["withSubstitutes"]?.size)
        assertEquals("Vanilla Lime", result["withSubstitutes"]?.get(0)?.name)
        
        assertEquals(1, result["withAlternatives"]?.size)
        assertEquals("Mimosa", result["withAlternatives"]?.get(0)?.name)
    }
    
    @Test
    fun `getAvailableCocktailsWithSubstitutions should return empty lists when no ingredients in stock`() {
        // Given
        val gimlet = Cocktail(
            id = 1,
            name = "Gimlet",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns emptyList()
        every { cocktailDataService.getAll() } returns listOf(gimlet)
        
        // When
        val result = cocktailSearchService.getAvailableCocktailsWithSubstitutions()
        
        // Then
        assertEquals(0, result["exact"]?.size)
        assertEquals(0, result["withSubstitutes"]?.size)
        assertEquals(0, result["withAlternatives"]?.size)
    }
    
    @Test
    fun `getAvailableCocktailsWithSubstitutions should prioritize exact matches over substitutes`() {
        // Given
        val vodka = Ingredient(1, "Vodka", IngredientType.SPIRIT, 40, true)
        val vanillaVodka = Ingredient(2, "Vanilla Vodka", IngredientType.SPIRIT, 35, true)
        val lime = Ingredient(3, "Lime Juice", IngredientType.JUICE, 0, true)
        
        vodka.substitutes.add(vanillaVodka)
        
        val gimlet = Cocktail(
            id = 1,
            name = "Gimlet",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0), CocktailIngredient(3, 30.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        every { ingredientDataService.getInStockIngredients() } returns listOf(vodka, vanillaVodka, lime)
        every { cocktailDataService.getAll() } returns listOf(gimlet)
        
        // When
        val result = cocktailSearchService.getAvailableCocktailsWithSubstitutions()
        
        // Then - Should be in exact, not in withSubstitutes
        assertEquals(1, result["exact"]?.size)
        assertEquals("Gimlet", result["exact"]?.get(0)?.name)
        assertEquals(0, result["withSubstitutes"]?.size)
        assertEquals(0, result["withAlternatives"]?.size)
    }
}
