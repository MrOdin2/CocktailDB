package com.cocktaildb.ingredient

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ingredients")
class IngredientController(
    private val ingredientDataService: IngredientDataService,
    private val patchIngredientService: PatchIngredientService,
) {

    @GetMapping
    fun getAllIngredients(): List<IngredientDTO> {
        return ingredientDataService.getAllIngredients().toDTOs()
    }

    @GetMapping("/{id}")
    fun getIngredientById(@PathVariable id: Long): ResponseEntity<IngredientDTO> {
        val ingredient = ingredientDataService.getIngredientById(id)
        return if (ingredient != null) {
            ResponseEntity.ok(ingredient.toDTO())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createIngredient(@RequestBody ingredientDTO: IngredientDTO): ResponseEntity<IngredientDTO> {
        val created = ingredientDataService.createIngredient(ingredientDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toDTO())
    }

    @PutMapping("/{id}")
    fun updateIngredient(
        @PathVariable id: Long,
        @RequestBody ingredientDTO: IngredientDTO
    ): ResponseEntity<IngredientDTO> {
        if (ingredientDTO.id == null || ingredientDTO.id != id) {
            return ResponseEntity.badRequest().build()
        }
        val updated = patchIngredientService.updateIngredient(ingredientDTO)
        return if (updated != null) {
            ResponseEntity.ok(updated.toDTO())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteIngredient(@PathVariable id: Long): ResponseEntity<Void> {
        return if (ingredientDataService.getIngredientById(id) != null) {
            ingredientDataService.deleteIngredient(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/in-stock")
    fun getInStockIngredients(): List<IngredientDTO> {
        return ingredientDataService.getInStockIngredients().toDTOs()
    }
}