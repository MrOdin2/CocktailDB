package com.cocktaildb.cocktail

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class CocktailRepositoryTest {
    
    @Autowired
    private lateinit var cocktailRepository: CocktailRepository
    
    @BeforeEach
    fun setup() {
        cocktailRepository.deleteAll()
    }
    
    @Test
    fun `should save and retrieve cocktail`() {
        // Given
        val cocktail = Cocktail(
            name = "Mojito",
            ingredients = mutableListOf(CocktailIngredient(1, 60.0)),
            steps = mutableListOf("Step 1", "Step 2"),
            notes = "Refreshing summer drink",
            tags = mutableListOf("refreshing", "summer"),
            abv = 15,
            baseSpirit = "Rum"
        )
        
        // When
        val saved = cocktailRepository.save(cocktail)
        val retrieved = cocktailRepository.findById(saved.id!!).orElse(null)
        
        // Then
        assertNotNull(retrieved)
        assertEquals("Mojito", retrieved.name)
        assertEquals(1, retrieved.ingredients.size)
        assertEquals(2, retrieved.steps.size)
        assertEquals("Refreshing summer drink", retrieved.notes)
        assertEquals(2, retrieved.tags.size)
        assertEquals(15, retrieved.abv)
        assertEquals("Rum", retrieved.baseSpirit)
    }
    
    @Test
    fun `should save cocktail with multiple ingredients`() {
        // Given
        val cocktail = Cocktail(
            name = "Long Island Iced Tea",
            ingredients = mutableListOf(
                CocktailIngredient(1, 15.0),
                CocktailIngredient(2, 15.0),
                CocktailIngredient(3, 15.0),
                CocktailIngredient(4, 15.0)
            ),
            steps = mutableListOf("Mix all ingredients"),
            tags = mutableListOf("strong"),
            abv = 22,
            baseSpirit = "Mixed"
        )
        
        // When
        val saved = cocktailRepository.save(cocktail)
        val retrieved = cocktailRepository.findById(saved.id!!).orElse(null)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(4, retrieved.ingredients.size)
    }
    
    @Test
    fun `should update cocktail`() {
        // Given
        val cocktail = cocktailRepository.save(
            Cocktail(
                name = "Old Name",
                ingredients = mutableListOf(),
                steps = mutableListOf("Step 1"),
                tags = mutableListOf(),
                abv = 15,
                baseSpirit = "Vodka"
            )
        )
        
        // When
        cocktail.name = "New Name"
        cocktail.abv = 20
        cocktailRepository.save(cocktail)
        
        val updated = cocktailRepository.findById(cocktail.id!!).orElse(null)
        
        // Then
        assertNotNull(updated)
        assertEquals("New Name", updated.name)
        assertEquals(20, updated.abv)
    }
    
    @Test
    fun `should delete cocktail`() {
        // Given
        val cocktail = cocktailRepository.save(
            Cocktail(
                name = "To Delete",
                ingredients = mutableListOf(),
                steps = mutableListOf("Step 1"),
                tags = mutableListOf(),
                abv = 15,
                baseSpirit = "Vodka"
            )
        )
        
        // When
        cocktailRepository.deleteById(cocktail.id!!)
        
        // Then
        assertFalse(cocktailRepository.existsById(cocktail.id!!))
    }
    
    @Test
    fun `should save cocktail without notes`() {
        // Given
        val cocktail = Cocktail(
            name = "Simple Cocktail",
            ingredients = mutableListOf(),
            steps = mutableListOf("Step 1"),
            notes = null,
            tags = mutableListOf(),
            abv = 15,
            baseSpirit = "Vodka"
        )
        
        // When
        val saved = cocktailRepository.save(cocktail)
        val retrieved = cocktailRepository.findById(saved.id!!).orElse(null)
        
        // Then
        assertNotNull(retrieved)
        assertNull(retrieved.notes)
    }
}
