package com.cocktaildb.controller

import com.cocktaildb.model.Cocktail
import com.cocktaildb.service.CocktailService
import com.cocktaildb.service.CocktailAvailabilityInfo
import com.cocktaildb.repository.CocktailRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cocktails")
class CocktailController(
    private val cocktailService: CocktailService,
    private val cocktailRepository: CocktailRepository
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
        return if (cocktailRepository.existsById(id)) {
            cocktailRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/available")
    fun getAvailableCocktails(): List<Cocktail> {
        return cocktailService.getAvailableCocktails()
    }
    
    @GetMapping("/available-with-substitutions")
    fun getAvailableCocktailsWithSubstitutions(): List<CocktailAvailabilityInfo> {
        return cocktailService.getAvailableCocktailsWithSubstitutions()
    }
    
    @GetMapping("/ingredient-impact")
    fun getIngredientImpact(): Map<Long, Pair<Int, Int>> {
        return cocktailService.getIngredientImpact()
    }
    
    @GetMapping("/search")
    fun searchCocktails(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) spirit: String?,
        @RequestParam(required = false) tags: List<String>?
    ): List<Cocktail> {
        return cocktailService.searchCocktails(name, spirit, tags)
    }
}
