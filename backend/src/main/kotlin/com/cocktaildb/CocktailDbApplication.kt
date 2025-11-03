package com.cocktaildb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CocktailDbApplication

fun main(args: Array<String>) {
    runApplication<CocktailDbApplication>(*args)
}
