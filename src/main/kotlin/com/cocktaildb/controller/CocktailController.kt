package com.cocktaildb.controller

import com.cocktaildb.model.Cocktail
import com.cocktaildb.service.CocktailService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cocktails")
@CrossOrigin(origins = ["http://localhost:4200"])
class CocktailController(
    private val cocktailService: CocktailService
) {
    
    @GetMapping
    fun getAllCocktails(): List<Cocktail> {
        return cocktailService.getAllCocktails()
    }
    
    @GetMapping("/{id}")
    fun getCocktailById(@PathVariable id: Long): ResponseEntity<Cocktail> {
        val cocktail = cocktailService.getCocktailById(id)
        return if (cocktail != null) {
            ResponseEntity.ok(cocktail)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping
    fun createCocktail(@RequestBody cocktail: Cocktail): ResponseEntity<Cocktail> {
        val created = cocktailService.createCocktail(cocktail)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
    
    @PutMapping("/{id}")
    fun updateCocktail(
        @PathVariable id: Long,
        @RequestBody cocktail: Cocktail
    ): ResponseEntity<Cocktail> {
        val updated = cocktailService.updateCocktail(id, cocktail)
        return if (updated != null) {
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteCocktail(@PathVariable id: Long): ResponseEntity<Void> {
        cocktailService.deleteCocktail(id)
        return ResponseEntity.noContent().build()
    }
    
    @GetMapping("/available")
    fun getAvailableCocktails(): List<Cocktail> {
        return cocktailService.getAvailableCocktails()
    }
}
