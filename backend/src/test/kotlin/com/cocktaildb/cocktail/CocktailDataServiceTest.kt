package com.cocktaildb.cocktail

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

class CocktailDataServiceTest {
    
    private lateinit var cocktailRepository: CocktailRepository
    private lateinit var cocktailDataService: CocktailDataService
    
    @BeforeEach
    fun setup() {
        cocktailRepository = mockk()
        cocktailDataService = CocktailDataService(cocktailRepository)
    }
    
    @Test
    fun `getAllCocktails should return all cocktails`() {
        // Given
        val cocktails = listOf(
            createTestCocktail(1, "Mojito"),
            createTestCocktail(2, "Martini")
        )
        every { cocktailRepository.findAll() } returns cocktails
        
        // When
        val result = cocktailDataService.getAllCocktails()
        
        // Then
        assertEquals(2, result.size)
        assertEquals("Mojito", result[0].name)
        assertEquals("Martini", result[1].name)
        verify { cocktailRepository.findAll() }
    }
    
    @Test
    fun `getCocktailById should return cocktail when exists`() {
        // Given
        val cocktail = createTestCocktail(1, "Mojito")
        every { cocktailRepository.findById(1) } returns Optional.of(cocktail)
        
        // When
        val result = cocktailDataService.getCocktailById(1)
        
        // Then
        assertNotNull(result)
        assertEquals("Mojito", result?.name)
        verify { cocktailRepository.findById(1) }
    }
    
    @Test
    fun `getCocktailById should return null when not exists`() {
        // Given
        every { cocktailRepository.findById(999) } returns Optional.empty()
        
        // When
        val result = cocktailDataService.getCocktailById(999)
        
        // Then
        assertNull(result)
        verify { cocktailRepository.findById(999) }
    }
    
    @Test
    fun `createCocktail should save and return cocktail`() {
        // Given
        val newCocktail = createTestCocktail(null, "New Cocktail")
        val savedCocktail = createTestCocktail(1, "New Cocktail")
        every { cocktailRepository.save(newCocktail) } returns savedCocktail
        
        // When
        val result = cocktailDataService.createCocktail(newCocktail)
        
        // Then
        assertNotNull(result.id)
        assertEquals("New Cocktail", result.name)
        verify { cocktailRepository.save(newCocktail) }
    }
    
    @Test
    fun `updateCocktail should update existing cocktail`() {
        // Given
        val existing = createTestCocktail(1, "Old Name")
        val update = createTestCocktail(1, "New Name")
        every { cocktailRepository.findById(1) } returns Optional.of(existing)
        every { cocktailRepository.save(any()) } returns update
        
        // When
        val result = cocktailDataService.updateCocktail(1, update)
        
        // Then
        assertNotNull(result)
        assertEquals("New Name", result?.name)
        verify { cocktailRepository.findById(1) }
        verify { cocktailRepository.save(any()) }
    }
    
    @Test
    fun `updateCocktail should return null when cocktail not exists`() {
        // Given
        val update = createTestCocktail(999, "New Name")
        every { cocktailRepository.findById(999) } returns Optional.empty()
        
        // When
        val result = cocktailDataService.updateCocktail(999, update)
        
        // Then
        assertNull(result)
        verify { cocktailRepository.findById(999) }
        verify(exactly = 0) { cocktailRepository.save(any()) }
    }
    
    @Test
    fun `deleteCocktail should return true when cocktail exists`() {
        // Given
        every { cocktailRepository.existsById(1) } returns true
        every { cocktailRepository.deleteById(1) } returns Unit
        
        // When
        val result = cocktailDataService.deleteCocktail(1)
        
        // Then
        assertTrue(result)
        verify { cocktailRepository.existsById(1) }
        verify { cocktailRepository.deleteById(1) }
    }
    
    @Test
    fun `deleteCocktail should return false when cocktail not exists`() {
        // Given
        every { cocktailRepository.existsById(999) } returns false
        every { cocktailRepository.deleteById(999) } returns Unit
        
        // When
        val result = cocktailDataService.deleteCocktail(999)
        
        // Then
        assertFalse(result)
        verify { cocktailRepository.existsById(999) }
        verify { cocktailRepository.deleteById(999) }
    }
    
    private fun createTestCocktail(id: Long?, name: String): Cocktail {
        return Cocktail(
            id = id,
            name = name,
            ingredients = mutableListOf(),
            steps = mutableListOf("Step 1"),
            notes = "Test notes",
            tags = mutableListOf("test"),
            abv = 15,
            baseSpirit = "Vodka"
        )
    }
}
