package com.cocktaildb.config

import com.cocktaildb.model.Cocktail
import com.cocktaildb.model.CocktailIngredient
import com.cocktaildb.model.Ingredient
import com.cocktaildb.model.IngredientType
import com.cocktaildb.repository.CocktailRepository
import com.cocktaildb.repository.IngredientRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val ingredientRepository: IngredientRepository,
    private val cocktailRepository: CocktailRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Only initialize data if the database is empty
        if (ingredientRepository.count() == 0L && cocktailRepository.count() == 0L) {
            // Create ingredients
            val ingredients = createIngredients()
            
            // Create cocktails
            createCocktails(ingredients)
        }
    }

    private fun createIngredients(): Map<String, Ingredient> {
        val ingredientsList = listOf(
            // Spirits
            Ingredient(name = "Vodka", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "White Rum", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Dark Rum", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Gin", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Tequila", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Whiskey", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Bourbon", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Cognac", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            
            // Liqueurs
            Ingredient(name = "Triple Sec", type = IngredientType.LIQUEUR, abv = 25, inStock = true),
            Ingredient(name = "Coffee Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Amaretto", type = IngredientType.LIQUEUR, abv = 28, inStock = false),
            Ingredient(name = "Blue Curaçao", type = IngredientType.LIQUEUR, abv = 24, inStock = false),
            Ingredient(name = "Campari", type = IngredientType.LIQUEUR, abv = 25, inStock = true),
            Ingredient(name = "Sweet Vermouth", type = IngredientType.LIQUEUR, abv = 18, inStock = true),
            Ingredient(name = "Dry Vermouth", type = IngredientType.LIQUEUR, abv = 18, inStock = true),
            
            // Juices
            Ingredient(name = "Lime Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Lemon Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Orange Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Pineapple Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Cranberry Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Tomato Juice", type = IngredientType.JUICE, abv = 0, inStock = false),
            
            // Sodas
            Ingredient(name = "Club Soda", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Tonic Water", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Ginger Beer", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Cola", type = IngredientType.SODA, abv = 0, inStock = true),
            
            // Syrups
            Ingredient(name = "Simple Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Grenadine", type = IngredientType.SYRUP, abv = 0, inStock = true),
            
            // Bitters
            Ingredient(name = "Angostura Bitters", type = IngredientType.BITTERS, abv = 45, inStock = true),
            
            // Garnishes
            Ingredient(name = "Mint Leaves", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Lime Wedge", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Lemon Twist", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Orange Slice", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Maraschino Cherry", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Olives", type = IngredientType.GARNISH, abv = 0, inStock = true),
            
            // Other
            Ingredient(name = "Sugar", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Salt", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Heavy Cream", type = IngredientType.OTHER, abv = 0, inStock = true)
        )
        
        val savedIngredients = ingredientRepository.saveAll(ingredientsList)
        return savedIngredients.associateBy { it.name }
    }

    private fun createCocktails(ingredients: Map<String, Ingredient>) {
        fun getIngredientId(name: String): Long {
            return ingredients[name]?.id 
                ?: throw IllegalStateException("Ingredient not found: $name")
        }
        
        val cocktails = listOf(
            // 1. Margarita
            Cocktail(
                name = "Margarita",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Salt"), "for rim")
                ),
                steps = mutableListOf(
                    "Rim glass with salt",
                    "Add tequila, triple sec, and lime juice to shaker with ice",
                    "Shake well",
                    "Strain into salt-rimmed glass",
                    "Garnish with lime wedge"
                ),
                notes = "A classic Mexican cocktail",
                tags = mutableListOf("sour", "refreshing", "classic")
            ),
            
            // 2. Mojito
            Cocktail(
                name = "Mojito",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "8-10 leaves"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Muddle mint leaves with simple syrup and lime juice in glass",
                    "Add rum and ice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with mint sprig"
                ),
                notes = "A refreshing Cuban classic",
                tags = mutableListOf("refreshing", "sweet", "minty")
            ),
            
            // 3. Old Fashioned
            Cocktail(
                name = "Old Fashioned",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add simple syrup and bitters to glass",
                    "Add bourbon and ice",
                    "Stir well",
                    "Garnish with orange slice and cherry"
                ),
                notes = "A timeless whiskey cocktail",
                tags = mutableListOf("spirit forward", "classic", "strong")
            ),
            
            // 4. Cosmopolitan
            Cocktail(
                name = "Cosmopolitan",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into martini glass",
                    "Garnish with lime wedge"
                ),
                notes = "Made famous by Sex and the City",
                tags = mutableListOf("sweet", "fruity", "modern")
            ),
            
            // 5. Daiquiri
            Cocktail(
                name = "Daiquiri",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into chilled coupe glass",
                    "Garnish with lime wheel"
                ),
                notes = "Ernest Hemingway's favorite",
                tags = mutableListOf("sour", "refreshing", "classic")
            ),
            
            // 6. Moscow Mule
            Cocktail(
                name = "Moscow Mule",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Ginger Beer"), "4 oz")
                ),
                steps = mutableListOf(
                    "Add vodka and lime juice to copper mug with ice",
                    "Top with ginger beer",
                    "Stir gently",
                    "Garnish with lime wedge"
                ),
                notes = "Traditionally served in a copper mug",
                tags = mutableListOf("refreshing", "spicy", "fizzy")
            ),
            
            // 7. Piña Colada
            Cocktail(
                name = "Piña Colada",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Heavy Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to blender with ice",
                    "Blend until smooth",
                    "Pour into hurricane glass",
                    "Garnish with pineapple wedge and cherry"
                ),
                notes = "The official drink of Puerto Rico",
                tags = mutableListOf("sweet", "creamy", "tropical")
            ),
            
            // 8. Gin and Tonic
            Cocktail(
                name = "Gin and Tonic",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Tonic Water"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add gin",
                    "Top with tonic water",
                    "Stir gently",
                    "Garnish with lime wedge"
                ),
                notes = "A simple and refreshing classic",
                tags = mutableListOf("refreshing", "fizzy", "simple")
            ),
            
            // 9. Mai Tai
            Cocktail(
                name = "Mai Tai",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Dark Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients except dark rum to shaker with ice",
                    "Shake well",
                    "Strain into glass with crushed ice",
                    "Float dark rum on top",
                    "Garnish with mint and lime"
                ),
                notes = "A Tiki classic from the 1940s",
                tags = mutableListOf("tropical", "sweet", "complex")
            ),
            
            // 10. Whiskey Sour
            Cocktail(
                name = "Whiskey Sour",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into rocks glass with ice",
                    "Garnish with cherry and orange slice"
                ),
                notes = "A perfect balance of sweet and sour",
                tags = mutableListOf("sour", "classic", "balanced")
            ),
            
            // 11. Cuba Libre
            Cocktail(
                name = "Cuba Libre",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Cola"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add rum and lime juice",
                    "Top with cola",
                    "Stir gently",
                    "Garnish with lime wedge"
                ),
                notes = "Rum and Coke with a twist",
                tags = mutableListOf("sweet", "simple", "fizzy")
            ),
            
            // 12. White Russian
            Cocktail(
                name = "White Russian",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Coffee Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Heavy Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add vodka and coffee liqueur to glass with ice",
                    "Float heavy cream on top",
                    "Stir gently before drinking"
                ),
                notes = "The Dude's favorite drink",
                tags = mutableListOf("creamy", "sweet", "dessert")
            ),
            
            // 13. Negroni
            Cocktail(
                name = "Negroni",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1 oz"),
                    CocktailIngredient(getIngredientId("Campari"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to glass with ice",
                    "Stir well",
                    "Garnish with orange slice"
                ),
                notes = "A bitter Italian aperitif",
                tags = mutableListOf("bitter", "spirit forward", "classic")
            ),
            
            // 14. Martini
            Cocktail(
                name = "Martini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add gin and vermouth to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled martini glass",
                    "Garnish with olives or lemon twist"
                ),
                notes = "Shaken or stirred - your choice",
                tags = mutableListOf("spirit forward", "classic", "elegant")
            ),
            
            // 15. Manhattan
            Cocktail(
                name = "Manhattan",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "1 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled coupe glass",
                    "Garnish with cherry"
                ),
                notes = "A sophisticated whiskey cocktail",
                tags = mutableListOf("spirit forward", "classic", "strong")
            ),
            
            // 16. Sidecar
            Cocktail(
                name = "Sidecar",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cognac"), "2 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with lemon twist"
                ),
                notes = "A classic brandy cocktail",
                tags = mutableListOf("sour", "classic", "elegant")
            ),
            
            // 17. Tom Collins
            Cocktail(
                name = "Tom Collins",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin, lemon juice, and simple syrup to shaker with ice",
                    "Shake well",
                    "Strain into Collins glass with ice",
                    "Top with club soda",
                    "Garnish with lemon slice and cherry"
                ),
                notes = "A refreshing gin fizz",
                tags = mutableListOf("refreshing", "fizzy", "sour")
            ),
            
            // 18. Screwdriver
            Cocktail(
                name = "Screwdriver",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add vodka",
                    "Top with orange juice",
                    "Stir well"
                ),
                notes = "Simple and delicious",
                tags = mutableListOf("simple", "fruity", "sweet")
            ),
            
            // 19. Tequila Sunrise
            Cocktail(
                name = "Tequila Sunrise",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add tequila and orange juice",
                    "Stir well",
                    "Slowly pour grenadine to create sunrise effect",
                    "Garnish with orange slice and cherry"
                ),
                notes = "Beautiful gradient effect",
                tags = mutableListOf("sweet", "fruity", "colorful")
            ),
            
            // 20. Dark and Stormy
            Cocktail(
                name = "Dark and Stormy",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Ginger Beer"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add lime juice and ginger beer",
                    "Float dark rum on top",
                    "Garnish with lime wedge"
                ),
                notes = "The national drink of Bermuda",
                tags = mutableListOf("refreshing", "spicy", "simple")
            )
        )
        
        cocktailRepository.saveAll(cocktails)
    }
}
