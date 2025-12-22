package com.cocktaildb.ingredient

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IngredientCsvServiceTest {

    private lateinit var ingredientDataService: IngredientDataService
    private lateinit var csvService: IngredientCsvService

    @BeforeEach
    fun setup() {
        ingredientDataService = mockk()
        csvService = IngredientCsvService(ingredientDataService)
    }

    @Test
    fun `exportToCsv should generate valid CSV with header`() {
        // Given
        val vodka = Ingredient(id = 1L, name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        val gin = Ingredient(id = 2L, name = "Gin", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        vodka.substitutes.add(gin)

        val ingredients = listOf(vodka, gin)
        every { ingredientDataService.getAllIngredients() } returns ingredients

        // When
        val csv = csvService.exportToCsv()

        // Then
        assertTrue(csv.contains("name,type,abv,inStock,substituteNames,alternativeNames"))
        assertTrue(csv.contains("Vodka,SPIRIT,40,true"))
        assertTrue(csv.contains("Gin,SPIRIT,40,true"))
    }

    @Test
    fun `exportToCsv should include substitute and alternative names`() {
        // Given
        val vodka = Ingredient(id = 1L, name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        val gin = Ingredient(id = 2L, name = "Gin", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        val whiskey = Ingredient(id = 3L, name = "Whiskey", type = IngredientType.SPIRIT, abv = 40, inStock = false)
        
        vodka.substitutes.add(gin)
        vodka.alternatives.add(whiskey)

        every { ingredientDataService.getAllIngredients() } returns listOf(vodka)

        // When
        val csv = csvService.exportToCsv()

        // Then
        assertTrue(csv.contains("Gin"))
        assertTrue(csv.contains("Whiskey"))
    }

    @Test
    fun `exportToCsv should escape CSV special characters`() {
        // Given
        val ingredient = Ingredient(
            id = 1L,
            name = "Ingredient, With Comma",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        every { ingredientDataService.getAllIngredients() } returns listOf(ingredient)

        // When
        val csv = csvService.exportToCsv()

        // Then
        assertTrue(csv.contains("\"Ingredient, With Comma\""))
    }

    @Test
    fun `importFromCsvContent should import valid ingredient`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,40,true,,
        """.trimIndent()

        val createdIngredient = Ingredient(id = 1L, name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)

        every { ingredientDataService.getAllIngredients() } returns emptyList()
        every { ingredientDataService.createIngredient(any()) } returns createdIngredient

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(1, result.imported.size)
        assertEquals(0, result.errors.size)
        assertEquals("Vodka", result.imported[0].name)
        verify(exactly = 1) { ingredientDataService.createIngredient(any()) }
    }

    @Test
    fun `importFromCsvContent should reject ingredient with duplicate name`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,40,true,,
        """.trimIndent()

        val existingIngredient = Ingredient(id = 1L, name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        every { ingredientDataService.getAllIngredients() } returns listOf(existingIngredient)

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("already exists"))
    }

    @Test
    fun `importFromCsvContent should reject ingredient with invalid type`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,INVALID_TYPE,40,true,,
        """.trimIndent()

        every { ingredientDataService.getAllIngredients() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Invalid ingredient type"))
    }

    @Test
    fun `importFromCsvContent should reject ingredient with invalid ABV`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,invalid,true,,
        """.trimIndent()

        every { ingredientDataService.getAllIngredients() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Invalid ABV"))
    }

    @Test
    fun `importFromCsvContent should reject ingredient with ABV out of range`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,150,true,,
        """.trimIndent()

        every { ingredientDataService.getAllIngredients() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Invalid ABV"))
    }

    @Test
    fun `importFromCsvContent should reject ingredient with invalid inStock value`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,40,maybe,,
        """.trimIndent()

        every { ingredientDataService.getAllIngredients() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Invalid inStock"))
    }

    @Test
    fun `importFromCsvContent should create ingredients without relationships`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,40,true,,
            Gin,SPIRIT,40,true,,
        """.trimIndent()

        val vodka = Ingredient(id = 1L, name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        val gin = Ingredient(id = 2L, name = "Gin", type = IngredientType.SPIRIT, abv = 40, inStock = true)

        every { ingredientDataService.getAllIngredients() } returns emptyList()
        every { ingredientDataService.createIngredient(any()) } returnsMany listOf(vodka, gin)

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(2, result.imported.size)
        assertEquals(0, result.errors.size)
        verify(exactly = 2) { ingredientDataService.createIngredient(any()) }
    }

    @Test
    fun `importFromCsvContent should handle empty file`() {
        // Given
        val csvContent = ""

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Empty file"))
    }

    @Test
    fun `importFromCsvContent should reject row with insufficient columns`() {
        // Given
        val csvContent = """
            name,type,abv,inStock,substituteNames,alternativeNames
            Vodka,SPIRIT,40
        """.trimIndent()

        every { ingredientDataService.getAllIngredients() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Invalid format"))
    }
}
