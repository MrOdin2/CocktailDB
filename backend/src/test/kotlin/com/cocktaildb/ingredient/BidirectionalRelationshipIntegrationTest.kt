package com.cocktaildb.ingredient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BidirectionalRelationshipIntegrationTest {

    @Autowired
    private lateinit var ingredientDataService: IngredientDataService

    @Autowired
    private lateinit var patchIngredientService: PatchIngredientService

    @Autowired
    private lateinit var ingredientRepository: IngredientRepository

    @BeforeEach
    fun setup() {
        ingredientRepository.deleteAll()
    }

    @Test
    fun `adding Prosecco as substitute for Champagne should automatically add reverse relationship`() {
        // Given: Create Champagne
        val champagneDTO = IngredientDTO(
            id = null,
            name = "Champagne",
            type = IngredientType.WINE,
            abv = 12,
            inStock = true
        )
        val champagne = ingredientDataService.createIngredient(champagneDTO)

        // When: Create Prosecco with Champagne as substitute
        val proseccoDTO = IngredientDTO(
            id = null,
            name = "Prosecco",
            type = IngredientType.WINE,
            abv = 11,
            inStock = true,
            substituteIds = setOf(champagne.id!!)
        )
        val prosecco = ingredientDataService.createIngredient(proseccoDTO)

        // Then: Both should have each other as substitutes
        val reloadedChampagne = ingredientDataService.getIngredientById(champagne.id!!)!!
        val reloadedProsecco = ingredientDataService.getIngredientById(prosecco.id!!)!!

        assertTrue(reloadedProsecco.substitutes.any { it.id == champagne.id })
        assertTrue(reloadedChampagne.substitutes.any { it.id == prosecco.id })
    }

    @Test
    fun `updating ingredient to add alternative should create bidirectional relationship`() {
        // Given: Create two ingredients
        val vodkaDTO = IngredientDTO(
            id = null,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        val vodka = ingredientDataService.createIngredient(vodkaDTO)

        val vanillaVodkaDTO = IngredientDTO(
            id = null,
            name = "Vanilla Vodka",
            type = IngredientType.SPIRIT,
            abv = 35,
            inStock = true
        )
        val vanillaVodka = ingredientDataService.createIngredient(vanillaVodkaDTO)

        // When: Update Vodka to add Vanilla Vodka as alternative
        val updatedVodkaDTO = IngredientDTO(
            id = vodka.id,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true,
            alternativeIds = setOf(vanillaVodka.id!!)
        )
        patchIngredientService.updateIngredient(vodka.id!!, updatedVodkaDTO)

        // Then: Both should have each other as alternatives
        val reloadedVodka = ingredientDataService.getIngredientById(vodka.id!!)!!
        val reloadedVanillaVodka = ingredientDataService.getIngredientById(vanillaVodka.id!!)!!

        assertTrue(reloadedVodka.alternatives.any { it.id == vanillaVodka.id })
        assertTrue(reloadedVanillaVodka.alternatives.any { it.id == vodka.id })
    }

    @Test
    fun `removing substitute relationship should remove it bidirectionally`() {
        // Given: Create two ingredients with substitute relationship
        val ginDTO = IngredientDTO(
            id = null,
            name = "Gin",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true
        )
        val gin = ingredientDataService.createIngredient(ginDTO)

        val vodkaDTO = IngredientDTO(
            id = null,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true,
            substituteIds = setOf(gin.id!!)
        )
        val vodka = ingredientDataService.createIngredient(vodkaDTO)

        // Verify bidirectional relationship exists
        val initialVodka = ingredientDataService.getIngredientById(vodka.id!!)!!
        val initialGin = ingredientDataService.getIngredientById(gin.id!!)!!
        assertTrue(initialVodka.substitutes.any { it.id == gin.id })
        assertTrue(initialGin.substitutes.any { it.id == vodka.id })

        // When: Remove the substitute relationship
        val updatedVodkaDTO = IngredientDTO(
            id = vodka.id,
            name = "Vodka",
            type = IngredientType.SPIRIT,
            abv = 40,
            inStock = true,
            substituteIds = emptySet()
        )
        patchIngredientService.updateIngredient(vodka.id!!, updatedVodkaDTO)

        // Then: Both should no longer have each other as substitutes
        val finalVodka = ingredientDataService.getIngredientById(vodka.id!!)!!
        val finalGin = ingredientDataService.getIngredientById(gin.id!!)!!

        assertEquals(0, finalVodka.substitutes.size)
        assertEquals(0, finalGin.substitutes.size)
    }

    @Test
    fun `deleting ingredient should remove it from all relationships`() {
        // Given: Create three ingredients with relationships
        val champagneDTO = IngredientDTO(
            id = null,
            name = "Champagne",
            type = IngredientType.WINE,
            abv = 12,
            inStock = true
        )
        val champagne = ingredientDataService.createIngredient(champagneDTO)

        val proseccoDTO = IngredientDTO(
            id = null,
            name = "Prosecco",
            type = IngredientType.WINE,
            abv = 11,
            inStock = true,
            substituteIds = setOf(champagne.id!!)
        )
        val prosecco = ingredientDataService.createIngredient(proseccoDTO)

        val cavaDTO = IngredientDTO(
            id = null,
            name = "Cava",
            type = IngredientType.WINE,
            abv = 12,
            inStock = true,
            alternativeIds = setOf(champagne.id!!)
        )
        val cava = ingredientDataService.createIngredient(cavaDTO)

        // Verify relationships exist
        val initialChampagne = ingredientDataService.getIngredientById(champagne.id!!)!!
        assertTrue(initialChampagne.substitutes.size > 0 || initialChampagne.alternatives.size > 0)

        // When: Delete Champagne
        ingredientDataService.deleteIngredient(champagne.id!!)

        // Then: Other ingredients should no longer reference Champagne
        val finalProsecco = ingredientDataService.getIngredientById(prosecco.id!!)!!
        val finalCava = ingredientDataService.getIngredientById(cava.id!!)!!

        assertEquals(0, finalProsecco.substitutes.size)
        assertEquals(0, finalCava.alternatives.size)
    }
}
