package com.cocktaildb.repository

import com.cocktaildb.model.Cocktail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CocktailRepository : JpaRepository<Cocktail, Long>
