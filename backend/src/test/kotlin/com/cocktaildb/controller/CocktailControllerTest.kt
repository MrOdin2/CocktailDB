package com.cocktaildb.controller

import com.cocktaildb.cocktail.Cocktail
import com.cocktaildb.cocktail.CocktailController
import com.cocktaildb.cocktail.CocktailIngredient
import com.cocktaildb.cocktail.CocktailRepository
import com.cocktaildb.ingredient.Ingredient
import com.cocktaildb.ingredient.IngredientRepository
import com.cocktaildb.ingredient.IngredientType
import com.cocktaildb.security.SessionService
import com.cocktaildb.security.UserRole
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CocktailControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @Autowired
    private lateinit var cocktailRepository: CocktailRepository
    
    @Autowired
    private lateinit var ingredientRepository: IngredientRepository
    
    @Autowired
    private lateinit var sessionService: SessionService
    
    private lateinit var vodka: Ingredient
    private lateinit var lime: Ingredient
    private lateinit var adminSessionId: String
    
    @BeforeEach
    fun setup() {
        cocktailRepository.deleteAll()
        ingredientRepository.deleteAll()
        
        vodka = ingredientRepository.save(
            Ingredient(name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        )
        lime = ingredientRepository.save(
            Ingredient(name = "Lime Juice", type = IngredientType.JUICE, abv = 0, inStock = true)
        )
        
        // Create admin session for authenticated requests
        adminSessionId = sessionService.createSession(UserRole.ADMIN)
    }
    
    @Test
    fun `getAllCocktails should return all cocktails`() {
        // Given
        cocktailRepository.save(createTestCocktail("Mojito"))
        cocktailRepository.save(createTestCocktail("Martini"))
        
        // When/Then - GET /api/cocktails requires auth (path doesn't match /api/cocktails/ pattern in filter)
        mockMvc.perform(
            get("/api/cocktails")
                .cookie(Cookie("sessionId", adminSessionId))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }
    
    @Test
    fun `getCocktailById should return cocktail when exists`() {
        // Given
        val cocktail = cocktailRepository.save(createTestCocktail("Mojito"))
        
        // When/Then
        mockMvc.perform(get("/api/cocktails/${cocktail.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Mojito"))
    }
    
    @Test
    fun `getCocktailById should return 404 when not exists`() {
        // When/Then
        mockMvc.perform(get("/api/cocktails/999"))
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `createCocktail should create new cocktail`() {
        // Given
        val newCocktail = mapOf(
            "name" to "New Cocktail",
            "ingredients" to listOf(
                mapOf("ingredientId" to vodka.id, "measureMl" to 60.0)
            ),
            "steps" to listOf("Step 1"),
            "notes" to "Test notes",
            "tags" to listOf("test"),
            "abv" to 20,
            "baseSpirit" to "Vodka"
        )
        
        // When/Then
        mockMvc.perform(
            post("/api/cocktails")
                .cookie(Cookie("sessionId", adminSessionId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCocktail))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("New Cocktail"))
            .andExpect(jsonPath("$.id").exists())
    }
    
    @Test
    fun `updateCocktail should update existing cocktail`() {
        // Given
        val cocktail = cocktailRepository.save(createTestCocktail("Old Name"))
        val update = mapOf(
            "name" to "New Name",
            "ingredients" to listOf(
                mapOf("ingredientId" to vodka.id, "measureMl" to 60.0)
            ),
            "steps" to listOf("Step 1"),
            "notes" to "Updated notes",
            "tags" to listOf("updated"),
            "abv" to 25,
            "baseSpirit" to "Vodka"
        )
        
        // When/Then
        mockMvc.perform(
            put("/api/cocktails/${cocktail.id}")
                .cookie(Cookie("sessionId", adminSessionId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("New Name"))
    }
    
    @Test
    fun `updateCocktail should return 404 when not exists`() {
        // Given
        val update = mapOf(
            "name" to "New Name",
            "ingredients" to listOf<Map<String, Any>>(),
            "steps" to listOf("Step 1"),
            "tags" to listOf<String>(),
            "abv" to 25,
            "baseSpirit" to "Vodka"
        )
        
        // When/Then
        mockMvc.perform(
            put("/api/cocktails/999")
                .cookie(Cookie("sessionId", adminSessionId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
        )
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `deleteCocktail should delete existing cocktail`() {
        // Given
        val cocktail = cocktailRepository.save(createTestCocktail("To Delete"))
        
        // When/Then
        mockMvc.perform(
            delete("/api/cocktails/${cocktail.id}")
                .cookie(Cookie("sessionId", adminSessionId))
        )
            .andExpect(status().isOk)
    }
    
    @Test
    fun `deleteCocktail should return 404 when not exists`() {
        // When/Then
        mockMvc.perform(
            delete("/api/cocktails/999")
                .cookie(Cookie("sessionId", adminSessionId))
        )
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `getAvailableCocktails should return only cocktails with all ingredients in stock`() {
        // Given
        val availableCocktail = Cocktail(
            name = "Available",
            ingredients = mutableListOf(CocktailIngredient(vodka.id!!, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Vodka"
        )
        
        val outOfStockIngredient = ingredientRepository.save(
            Ingredient(name = "Rum", type = IngredientType.SPIRIT, abv = 40, inStock = false)
        )
        
        val unavailableCocktail = Cocktail(
            name = "Unavailable",
            ingredients = mutableListOf(CocktailIngredient(outOfStockIngredient.id!!, 60.0)),
            steps = mutableListOf("Mix"),
            tags = mutableListOf(),
            abv = 20,
            baseSpirit = "Rum"
        )
        
        cocktailRepository.save(availableCocktail)
        cocktailRepository.save(unavailableCocktail)
        
        // When/Then
        mockMvc.perform(get("/api/cocktails/available"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Available"))
    }
    
    @Test
    fun `searchCocktails should filter by name`() {
        // Given
        cocktailRepository.save(createTestCocktail("Mojito"))
        cocktailRepository.save(createTestCocktail("Martini"))
        
        // When/Then
        mockMvc.perform(get("/api/cocktails/search?name=moj"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Mojito"))
    }
    
    private fun createTestCocktail(name: String): Cocktail {
        return Cocktail(
            name = name,
            ingredients = mutableListOf(CocktailIngredient(vodka.id!!, 60.0)),
            steps = mutableListOf("Step 1"),
            notes = "Test notes",
            tags = mutableListOf("test"),
            abv = 20,
            baseSpirit = "Vodka"
        )
    }
}
