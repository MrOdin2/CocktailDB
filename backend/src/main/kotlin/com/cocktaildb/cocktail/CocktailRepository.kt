package com.cocktaildb.cocktail

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CocktailRepository : JpaRepository<Cocktail, Long>