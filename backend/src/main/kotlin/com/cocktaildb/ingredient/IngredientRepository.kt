package com.cocktaildb.ingredient

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByInStock(inStock: Boolean): List<Ingredient>
}