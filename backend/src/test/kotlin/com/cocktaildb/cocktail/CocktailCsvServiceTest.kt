package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.Ingredient
import com.cocktaildb.ingredient.IngredientRepository
import com.cocktaildb.ingredient.IngredientType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CocktailCsvServiceTest {

    private lateinit var cocktailDataService: CocktailDataService
    private lateinit var patchCocktailService: PatchCocktailService
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var csvService: CocktailCsvService

    private lateinit var vodka: Ingredient
    private lateinit var lime: Ingredient

    @BeforeEach
    fun setup() {
        cocktailDataService = mockk()
        patchCocktailService = mockk()
        ingredientRepository = mockk()
        csvService = CocktailCsvService(cocktailDataService, patchCocktailService, ingredientRepository)

        vodka = Ingredient(id = 1L, name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true)
        lime = Ingredient(id = 2L, name = "Lime Juice", type = IngredientType.JUICE, abv = 0, inStock = true)
    }

    @Test
    fun `exportToCsv should generate valid CSV with header`() {
        // Given
        val cocktails = listOf(
            Cocktail(
                id = 1L,
                name = "Test Cocktail",
                ingredients = mutableListOf(
                    CocktailIngredient(ingredientId = 1L, measureMl = 60.0),
                    CocktailIngredient(ingredientId = 2L, measureMl = 30.0)
                ),
                steps = mutableListOf("Step 1", "Step 2"),
                notes = "Test notes",
                tags = mutableListOf("test", "classic"),
                glasswareTypes = mutableListOf("Highball"),
                iceTypes = mutableListOf("Cubed")
            )
        )
        every { cocktailDataService.getAllCocktails() } returns cocktails
        every { ingredientRepository.findAll() } returns listOf(vodka, lime)

        // When
        val csv = csvService.exportToCsv()

        // Then
        assertTrue(csv.contains("name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId"))
        assertTrue(csv.contains("Test Cocktail"))
        assertTrue(csv.contains("Vodka;Lime Juice"))
        assertTrue(csv.contains("60.0;30.0"))
        assertTrue(csv.contains("Step 1|Step 2"))
        assertTrue(csv.contains("test;classic"))
    }

    @Test
    fun `exportToCsv should escape CSV special characters`() {
        // Given
        val cocktails = listOf(
            Cocktail(
                id = 1L,
                name = "Test, With Comma",
                ingredients = mutableListOf(CocktailIngredient(ingredientId = 1L, measureMl = 60.0)),
                steps = mutableListOf("Step 1"),
                notes = "Notes with \"quotes\"",
                tags = mutableListOf()
            )
        )
        every { cocktailDataService.getAllCocktails() } returns cocktails
        every { ingredientRepository.findAll() } returns listOf(vodka)

        // When
        val csv = csvService.exportToCsv()

        // Then
        assertTrue(csv.contains("\"Test, With Comma\""))
        assertTrue(csv.contains("\"Notes with \"\"quotes\"\"\""))
    }

    @Test
    fun `importFromCsvContent should import valid cocktail`() {
        // Given
        val csvContent = """
            name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
            Mojito,Vodka;Lime Juice,60;30,Shake well|Serve,Great drink,refreshing;summer,Highball,Crushed,
        """.trimIndent()

        every { ingredientRepository.findAll() } returns listOf(vodka, lime)
        every { cocktailDataService.getAllCocktails() } returns emptyList()
        every { patchCocktailService.createCocktail(any()) } answers {
            val cocktail = firstArg<Cocktail>()
            cocktail.copy(id = 1L)
        }

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(1, result.imported.size)
        assertEquals(0, result.errors.size)
        assertEquals("Mojito", result.imported[0].name)
        verify(exactly = 1) { patchCocktailService.createCocktail(any()) }
    }

    @Test
    fun `importFromCsvContent should reject cocktail with missing ingredient`() {
        // Given
        val csvContent = """
            name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
            Mojito,NonExistentIngredient,60,Shake well,Great drink,refreshing,Highball,Crushed,
        """.trimIndent()

        every { ingredientRepository.findAll() } returns listOf(vodka, lime)
        every { cocktailDataService.getAllCocktails() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Ingredient not found"))
    }

    @Test
    fun `importFromCsvContent should reject cocktail with duplicate name`() {
        // Given
        val csvContent = """
            name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
            Existing Cocktail,Vodka,60,Shake well,Great drink,refreshing,Highball,Crushed,
        """.trimIndent()

        val existingCocktail = Cocktail(
            id = 1L,
            name = "Existing Cocktail",
            ingredients = mutableListOf(),
            steps = mutableListOf()
        )

        every { ingredientRepository.findAll() } returns listOf(vodka)
        every { cocktailDataService.getAllCocktails() } returns listOf(existingCocktail)

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("already exists"))
    }

    @Test
    fun `importFromCsvContent should reject cocktail with mismatched ingredient counts`() {
        // Given
        val csvContent = """
            name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
            Bad Cocktail,Vodka;Lime Juice,60,Shake well,Great drink,refreshing,Highball,Crushed,
        """.trimIndent()

        every { ingredientRepository.findAll() } returns listOf(vodka, lime)
        every { cocktailDataService.getAllCocktails() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("count mismatch"))
    }

    @Test
    fun `importFromCsvContent should reject cocktail with invalid amount`() {
        // Given
        val csvContent = """
            name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
            Bad Cocktail,Vodka,invalid,Shake well,Great drink,refreshing,Highball,Crushed,
        """.trimIndent()

        every { ingredientRepository.findAll() } returns listOf(vodka)
        every { cocktailDataService.getAllCocktails() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Invalid amount"))
    }

    @Test
    fun `importFromCsvContent should accept negative amounts for count-based items`() {
        // Given
        val csvContent = """
            name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
            Garnished Cocktail,Vodka;Lime Juice,60;-3,Shake well|Garnish with 3 lime slices,Great drink,refreshing,Highball,Crushed,
        """.trimIndent()

        every { ingredientRepository.findAll() } returns listOf(vodka, lime)
        every { cocktailDataService.getAllCocktails() } returns emptyList()
        every { patchCocktailService.createCocktail(any()) } answers {
            val cocktail = firstArg<Cocktail>()
            cocktail.copy(id = 1L)
        }

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(1, result.imported.size)
        assertEquals(0, result.errors.size)
        assertEquals("Garnished Cocktail", result.imported[0].name)
        assertEquals(2, result.imported[0].ingredients.size)
        assertEquals(-3.0, result.imported[0].ingredients[1].measureMl) // Negative value for count
        verify(exactly = 1) { patchCocktailService.createCocktail(any()) }
    }

    @Test
    fun `importFromCsvContent should handle empty file`() {
        // Given
        val csvContent = ""
        
        every { ingredientRepository.findAll() } returns emptyList()

        // When
        val result = csvService.importFromCsvContent(csvContent)

        // Then
        assertEquals(0, result.imported.size)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors[0].message.contains("Empty file"))
    }
}
