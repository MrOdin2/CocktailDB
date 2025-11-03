package com.cocktaildb.controller

import com.cocktaildb.model.Ingredient
import com.cocktaildb.service.IngredientService
import com.cocktaildb.repository.IngredientRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = ["http://localhost:4200"])
class IngredientController(
    private val ingredientService: IngredientService,
    private val ingredientRepository: IngredientRepository
) {
    
    @GetMapping
    fun getAllIngredients(): List<Ingredient> {
        return ingredientService.getAllIngredients()
    }
    
    @GetMapping("/{id}")
    fun getIngredientById(@PathVariable id: Long): ResponseEntity<Ingredient> {
        val ingredient = ingredientService.getIngredientById(id)
        return if (ingredient != null) {
            ResponseEntity.ok(ingredient)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping
    fun createIngredient(@RequestBody ingredient: Ingredient): ResponseEntity<Ingredient> {
        val created = ingredientService.createIngredient(ingredient)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
    
    @PutMapping("/{id}")
    fun updateIngredient(
        @PathVariable id: Long,
        @RequestBody ingredient: Ingredient
    ): ResponseEntity<Ingredient> {
        val updated = ingredientService.updateIngredient(id, ingredient)
        return if (updated != null) {
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteIngredient(@PathVariable id: Long): ResponseEntity<Void> {
        return if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/in-stock")
    fun getInStockIngredients(): List<Ingredient> {
        return ingredientService.getInStockIngredients()
    }
}
