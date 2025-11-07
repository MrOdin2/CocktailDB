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
            Ingredient(name = "Spiced Rum", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Gold Rum", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Gin", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Tequila", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Mezcal", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Whiskey", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Bourbon", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Rye Whiskey", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Scotch", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Irish Whiskey", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Cognac", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Brandy", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Pisco", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Cachaca", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            Ingredient(name = "Absinthe", type = IngredientType.SPIRIT, abv = 60, inStock = true),
            Ingredient(name = "Sake", type = IngredientType.WINE, abv = 15, inStock = true),
            
            // Liqueurs
            Ingredient(name = "Triple Sec", type = IngredientType.LIQUEUR, abv = 25, inStock = true),
            Ingredient(name = "Cointreau", type = IngredientType.LIQUEUR, abv = 40, inStock = true),
            Ingredient(name = "Coffee Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Amaretto", type = IngredientType.LIQUEUR, abv = 28, inStock = true),
            Ingredient(name = "Blue Curaçao", type = IngredientType.LIQUEUR, abv = 24, inStock = true),
            Ingredient(name = "Campari", type = IngredientType.LIQUEUR, abv = 25, inStock = true),
            Ingredient(name = "Aperol", type = IngredientType.LIQUEUR, abv = 11, inStock = true),
            Ingredient(name = "Sweet Vermouth", type = IngredientType.LIQUEUR, abv = 18, inStock = true),
            Ingredient(name = "Dry Vermouth", type = IngredientType.LIQUEUR, abv = 18, inStock = true),
            Ingredient(name = "Elderflower Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Baileys Irish Cream", type = IngredientType.LIQUEUR, abv = 17, inStock = true),
            Ingredient(name = "Frangelico", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Benedictine", type = IngredientType.LIQUEUR, abv = 40, inStock = true),
            Ingredient(name = "Galliano", type = IngredientType.LIQUEUR, abv = 30, inStock = true),
            Ingredient(name = "Maraschino Liqueur", type = IngredientType.LIQUEUR, abv = 32, inStock = true),
            Ingredient(name = "Chambord", type = IngredientType.LIQUEUR, abv = 16, inStock = true),
            Ingredient(name = "Peach Schnapps", type = IngredientType.LIQUEUR, abv = 24, inStock = true),
            Ingredient(name = "Midori", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Sambuca", type = IngredientType.LIQUEUR, abv = 38, inStock = true),
            Ingredient(name = "Grand Marnier", type = IngredientType.LIQUEUR, abv = 40, inStock = true),
            Ingredient(name = "Sloe Gin", type = IngredientType.LIQUEUR, abv = 26, inStock = true),
            Ingredient(name = "Drambuie", type = IngredientType.LIQUEUR, abv = 40, inStock = true),
            Ingredient(name = "Cherry Liqueur", type = IngredientType.LIQUEUR, abv = 24, inStock = true),
            Ingredient(name = "Violet Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Blackberry Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Apple Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            Ingredient(name = "Apricot Brandy", type = IngredientType.LIQUEUR, abv = 24, inStock = true),
            Ingredient(name = "Green Chartreuse", type = IngredientType.LIQUEUR, abv = 55, inStock = true),
            Ingredient(name = "Yellow Chartreuse", type = IngredientType.LIQUEUR, abv = 40, inStock = true),
            Ingredient(name = "Dark Crème de Cacao", type = IngredientType.LIQUEUR, abv = 25, inStock = true),
            Ingredient(name = "White Crème de Menthe", type = IngredientType.LIQUEUR, abv = 25, inStock = true),
            Ingredient(name = "Falernum", type = IngredientType.LIQUEUR, abv = 11, inStock = true),
            Ingredient(name = "Allspice Dram", type = IngredientType.LIQUEUR, abv = 22, inStock = true),
            Ingredient(name = "Amaro Nonino", type = IngredientType.LIQUEUR, abv = 35, inStock = true),
            Ingredient(name = "Peach Liqueur", type = IngredientType.LIQUEUR, abv = 20, inStock = true),
            
            // Wines & Beers
            Ingredient(name = "Champagne", type = IngredientType.WINE, abv = 12, inStock = true),
            Ingredient(name = "Prosecco", type = IngredientType.WINE, abv = 11, inStock = true),
            Ingredient(name = "White Wine", type = IngredientType.WINE, abv = 12, inStock = true),
            Ingredient(name = "Red Wine", type = IngredientType.WINE, abv = 13, inStock = true),
            Ingredient(name = "Sherry", type = IngredientType.WINE, abv = 17, inStock = true),
            Ingredient(name = "Beer", type = IngredientType.BEER, abv = 5, inStock = true),
            Ingredient(name = "Coconut Rum", type = IngredientType.SPIRIT, abv = 21, inStock = true),
            Ingredient(name = "Islay Scotch", type = IngredientType.SPIRIT, abv = 40, inStock = true),
            
            // Juices
            Ingredient(name = "Lime Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Lemon Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Orange Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Pineapple Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Cranberry Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Tomato Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Grapefruit Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Apple Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Passion Fruit Juice", type = IngredientType.JUICE, abv = 0, inStock = true),
            Ingredient(name = "Coconut Cream", type = IngredientType.JUICE, abv = 0, inStock = true),
            
            // Sodas
            Ingredient(name = "Club Soda", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Tonic Water", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Ginger Beer", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Ginger Ale", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Cola", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Lemon-Lime Soda", type = IngredientType.SODA, abv = 0, inStock = true),
            Ingredient(name = "Sprite", type = IngredientType.SODA, abv = 0, inStock = true),
            
            // Syrups
            Ingredient(name = "Simple Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Grenadine", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Honey Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Agave Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Orgeat Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Maple Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Vanilla Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Demerara Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Ginger Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Passion Fruit Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Cane Syrup", type = IngredientType.SYRUP, abv = 0, inStock = true),
            Ingredient(name = "Cinnamon Sugar", type = IngredientType.SYRUP, abv = 0, inStock = true),
            
            // Bitters
            Ingredient(name = "Angostura Bitters", type = IngredientType.BITTERS, abv = 45, inStock = true),
            Ingredient(name = "Orange Bitters", type = IngredientType.BITTERS, abv = 45, inStock = true),
            Ingredient(name = "Peychaud's Bitters", type = IngredientType.BITTERS, abv = 35, inStock = true),
            
            // Garnishes
            Ingredient(name = "Mint Leaves", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Lime Wedge", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Lemon Twist", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Orange Slice", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Maraschino Cherry", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Olives", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Celery Stalk", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Cucumber", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Basil", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Rosemary", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Grapes", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Lime", type = IngredientType.GARNISH, abv = 0, inStock = true),
            Ingredient(name = "Pineapple", type = IngredientType.GARNISH, abv = 0, inStock = true),
            
            // Other
            Ingredient(name = "Sugar", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Salt", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Heavy Cream", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Milk", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Egg White", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Tabasco Sauce", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Worcestershire Sauce", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Black Pepper", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Cayenne Pepper", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Espresso", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Water", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Ice Cream", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Olive Brine", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Brown Sugar", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Butter", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Hot Water", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Orange Blossom Water", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Peach Purée", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Strawberry Purée", type = IngredientType.OTHER, abv = 0, inStock = true),
            Ingredient(name = "Watermelon Juice", type = IngredientType.OTHER, abv = 0, inStock = true)
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
            ),
            
            // 21. Bloody Mary
            Cocktail(
                name = "Bloody Mary",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Tomato Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Worcestershire Sauce"), "2 dashes"),
                    CocktailIngredient(getIngredientId("Tabasco Sauce"), "2 dashes"),
                    CocktailIngredient(getIngredientId("Salt"), "pinch"),
                    CocktailIngredient(getIngredientId("Black Pepper"), "pinch")
                ),
                steps = mutableListOf(
                    "Add all ingredients to glass with ice",
                    "Stir well",
                    "Garnish with celery stalk and lemon wedge"
                ),
                notes = "The ultimate brunch cocktail",
                tags = mutableListOf("savory", "brunch", "spicy")
            ),
            
            // 22. Espresso Martini
            Cocktail(
                name = "Espresso Martini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Coffee Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Espresso"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake vigorously",
                    "Strain into chilled martini glass",
                    "Garnish with coffee beans"
                ),
                notes = "Created by Dick Bradsell in London",
                tags = mutableListOf("coffee", "dessert", "energizing")
            ),
            
            // 23. Vodka Gimlet
            Cocktail(
                name = "Vodka Gimlet",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into chilled cocktail glass",
                    "Garnish with lime wheel"
                ),
                notes = "A clean and simple vodka cocktail",
                tags = mutableListOf("sour", "simple", "classic")
            ),
            
            // 24. Sea Breeze
            Cocktail(
                name = "Sea Breeze",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Grapefruit Juice"), "1 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add vodka, cranberry and grapefruit juice",
                    "Stir well",
                    "Garnish with lime wedge"
                ),
                notes = "A refreshing fruity vodka cocktail",
                tags = mutableListOf("fruity", "refreshing", "simple")
            ),
            
            // 25. Bay Breeze
            Cocktail(
                name = "Bay Breeze",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add all ingredients",
                    "Stir well",
                    "Garnish with pineapple wedge"
                ),
                notes = "Also known as a Hawaiian Sea Breeze",
                tags = mutableListOf("fruity", "tropical", "simple")
            ),
            
            // 26. Lemon Drop
            Cocktail(
                name = "Lemon Drop",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "for rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into sugar-rimmed glass",
                    "Garnish with lemon twist"
                ),
                notes = "A sweet and sour vodka cocktail",
                tags = mutableListOf("sweet", "sour", "citrus")
            ),            
            // 27. Appletini
            Cocktail(
                name = "Appletini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into chilled martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini variation",
                tags = mutableListOf("sweet", "fruity", "modern")
            ),            
            // 28. Chi-Chi
            Cocktail(
                name = "Chi-Chi",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Coconut Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to blender with ice",
                    "Blend until smooth",
                    "Pour into hurricane glass",
                    "Garnish with pineapple wedge"
                ),
                notes = "Vodka version of Piña Colada",
                tags = mutableListOf("tropical", "creamy", "sweet")
            ),            
            // 29. Harvey Wallbanger
            Cocktail(
                name = "Harvey Wallbanger",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Galliano"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add vodka and orange juice",
                    "Stir",
                    "Float Galliano on top",
                    "Garnish with orange slice"
                ),
                notes = "A 1970s classic",
                tags = mutableListOf("sweet", "fruity", "retro")
            ),            
            // 30. Kamikaze
            Cocktail(
                name = "Kamikaze",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into shot glass or rocks glass"
                ),
                notes = "A strong citrus shot or cocktail",
                tags = mutableListOf("strong", "citrus", "sharp")
            ),            
            // 31. Vodka Tonic
            Cocktail(
                name = "Vodka Tonic",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Tonic Water"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add vodka",
                    "Top with tonic water",
                    "Stir gently",
                    "Garnish with lime wedge"
                ),
                notes = "Simple and refreshing",
                tags = mutableListOf("simple", "refreshing", "fizzy")
            ),            
            // 32. Cape Codder
            Cocktail(
                name = "Cape Codder",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add vodka and cranberry juice",
                    "Stir well",
                    "Garnish with lime wedge"
                ),
                notes = "Also known as Vodka Cranberry",
                tags = mutableListOf("fruity", "simple", "tart")
            ),            
            // 33. Madras
            Cocktail(
                name = "Madras",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "1.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add all ingredients",
                    "Stir well",
                    "Garnish with lime wedge"
                ),
                notes = "Cranberry and orange vodka mix",
                tags = mutableListOf("fruity", "refreshing", "simple")
            ),            
            // 34. Vesper
            Cocktail(
                name = "Vesper",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "3 oz"),
                    CocktailIngredient(getIngredientId("Vodka"), "1 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled martini glass",
                    "Garnish with lemon twist"
                ),
                notes = "James Bond's signature drink",
                tags = mutableListOf("strong", "classic", "elegant")
            ),            
            // 35. Black Russian
            Cocktail(
                name = "Black Russian",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Coffee Liqueur"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add ingredients to glass with ice",
                    "Stir well"
                ),
                notes = "Simple and strong coffee cocktail",
                tags = mutableListOf("strong", "coffee", "simple")
            ),            
            // 36. Aviation
            Cocktail(
                name = "Aviation",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Maraschino Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with cherry"
                ),
                notes = "A pre-Prohibition classic",
                tags = mutableListOf("floral", "sour", "elegant")
            ),            
            // 37. Bee's Knees
            Cocktail(
                name = "Bee's Knees",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with lemon twist"
                ),
                notes = "A Prohibition-era classic",
                tags = mutableListOf("sweet", "sour", "balanced")
            ),            
            // 38. Bramble
            Cocktail(
                name = "Bramble",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add gin, lemon juice, and simple syrup to glass with crushed ice",
                    "Stir",
                    "Drizzle Chambord over top",
                    "Garnish with berries"
                ),
                notes = "Created by Dick Bradsell",
                tags = mutableListOf("fruity", "refreshing", "modern")
            ),            
            // 39. Corpse Reviver #2
            Cocktail(
                name = "Corpse Reviver #2",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Cointreau"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Absinthe"), "1 dash")
                ),
                steps = mutableListOf(
                    "Rinse glass with absinthe",
                    "Add remaining ingredients to shaker with ice",
                    "Shake well",
                    "Strain into absinthe-rinsed glass"
                ),
                notes = "Hair of the dog cocktail",
                tags = mutableListOf("complex", "herbal", "strong")
            ),            
            // 40. French 75
            Cocktail(
                name = "French 75",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Champagne"), "top")
                ),
                steps = mutableListOf(
                    "Add gin, lemon juice, and simple syrup to shaker with ice",
                    "Shake well",
                    "Strain into champagne flute",
                    "Top with champagne",
                    "Garnish with lemon twist"
                ),
                notes = "Named after a WWI French field gun",
                tags = mutableListOf("elegant", "bubbly", "refreshing")
            ),            
            // 41. Gimlet
            Cocktail(
                name = "Gimlet",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into chilled coupe",
                    "Garnish with lime wheel"
                ),
                notes = "A naval officer's drink",
                tags = mutableListOf("sour", "simple", "classic")
            ),            
            // 42. Last Word
            Cocktail(
                name = "Last Word",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Green Chartreuse"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Maraschino Liqueur"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with cherry"
                ),
                notes = "A pre-Prohibition equal parts cocktail",
                tags = mutableListOf("herbal", "complex", "balanced")
            ),            
            // 43. Southside
            Cocktail(
                name = "Southside",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "6-8")
                ),
                steps = mutableListOf(
                    "Muddle mint with simple syrup",
                    "Add gin and lime juice",
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with mint"
                ),
                notes = "A minty gin sour",
                tags = mutableListOf("minty", "refreshing", "herbal")
            ),            
            // 44. Singapore Sling
            Cocktail(
                name = "Singapore Sling",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Cherry Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Cointreau"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Benedictine"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into highball glass with ice",
                    "Garnish with pineapple and cherry"
                ),
                notes = "Created at Raffles Hotel Singapore",
                tags = mutableListOf("tropical", "complex", "sweet")
            ),            
            // 45. Clover Club
            Cocktail(
                name = "Clover Club",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker without ice",
                    "Dry shake for 30 seconds",
                    "Add ice and shake again",
                    "Strain into coupe glass"
                ),
                notes = "A frothy pre-Prohibition cocktail",
                tags = mutableListOf("frothy", "sour", "elegant")
            ),            
            // 46. Caipirinha
            Cocktail(
                name = "Caipirinha",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cachaca"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime"), "1 whole"),
                    CocktailIngredient(getIngredientId("Sugar"), "2 tsp")
                ),
                steps = mutableListOf(
                    "Quarter lime and muddle with sugar in glass",
                    "Fill glass with ice",
                    "Add cachaça",
                    "Stir well"
                ),
                notes = "Brazil's national cocktail",
                tags = mutableListOf("refreshing", "simple", "tangy")
            ),            
            // 47. Hurricane
            Cocktail(
                name = "Hurricane",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Passion Fruit Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into hurricane glass",
                    "Garnish with orange and cherry"
                ),
                notes = "New Orleans party drink",
                tags = mutableListOf("tropical", "fruity", "strong")
            ),            
            // 48. Painkiller
            Cocktail(
                name = "Painkiller",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Coconut Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into glass",
                    "Garnish with nutmeg and pineapple"
                ),
                notes = "Pusser's Rum signature cocktail",
                tags = mutableListOf("tropical", "creamy", "sweet")
            ),            
            // 49. Planter's Punch
            Cocktail(
                name = "Planter's Punch",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Water"), "1 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add all ingredients to glass with ice",
                    "Stir well",
                    "Garnish with orange slice and cherry"
                ),
                notes = "One of sour, two of sweet, three of strong, four of weak",
                tags = mutableListOf("balanced", "refreshing", "classic")
            ),            
            // 50. Zombie
            Cocktail(
                name = "Zombie",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Gold Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Dark Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Passion Fruit Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Absinthe"), "1 dash")
                ),
                steps = mutableListOf(
                    "Add all ingredients except dark rum to shaker with ice",
                    "Shake well",
                    "Pour into tiki mug",
                    "Float dark rum on top"
                ),
                notes = "Limit two per customer",
                tags = mutableListOf("strong", "tropical", "dangerous")
            ),            
            // 51. Between the Sheets
            Cocktail(
                name = "Between the Sheets",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Cognac"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with lemon twist"
                ),
                notes = "A sophisticated equal parts cocktail",
                tags = mutableListOf("strong", "citrus", "elegant")
            ),            
            // 52. El Presidente
            Cocktail(
                name = "El Presidente",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Blue Curaçao"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into coupe",
                    "Garnish with orange twist"
                ),
                notes = "A Cuban classic from the 1920s",
                tags = mutableListOf("elegant", "balanced", "aromatic")
            ),            
            // 53. Mary Pickford
            Cocktail(
                name = "Mary Pickford",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Maraschino Liqueur"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe",
                    "Garnish with cherry"
                ),
                notes = "Named after the silent film star",
                tags = mutableListOf("sweet", "fruity", "classic")
            ),            
            // 54. Hot Buttered Rum
            Cocktail(
                name = "Hot Buttered Rum",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Butter"), "1 tbsp"),
                    CocktailIngredient(getIngredientId("Brown Sugar"), "1 tbsp"),
                    CocktailIngredient(getIngredientId("Hot Water"), "4 oz")
                ),
                steps = mutableListOf(
                    "Add butter, sugar, and spices to mug",
                    "Add hot water and stir",
                    "Add rum and stir again",
                    "Garnish with cinnamon stick"
                ),
                notes = "A warming winter cocktail",
                tags = mutableListOf("hot", "comforting", "spiced")
            ),            
            // 55. Rum Punch
            Cocktail(
                name = "Rum Punch",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into glass",
                    "Garnish with fruit"
                ),
                notes = "A fruity Caribbean classic",
                tags = mutableListOf("tropical", "fruity", "sweet")
            ),            
            // 56. Paloma
            Cocktail(
                name = "Paloma",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Grapefruit Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Salt"), "for rim")
                ),
                steps = mutableListOf(
                    "Rim glass with salt",
                    "Fill with ice",
                    "Add tequila, grapefruit juice, lime juice, and syrup",
                    "Stir well",
                    "Garnish with grapefruit wedge"
                ),
                notes = "Mexico's most popular cocktail",
                tags = mutableListOf("refreshing", "citrus", "classic")
            ),            
            // 57. Tommy's Margarita
            Cocktail(
                name = "Tommy's Margarita",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Agave Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into glass with ice",
                    "Garnish with lime wheel"
                ),
                notes = "A modern margarita variation",
                tags = mutableListOf("sour", "simple", "agave")
            ),            
            // 58. Frozen Margarita
            Cocktail(
                name = "Frozen Margarita",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to blender with ice",
                    "Blend until slushy",
                    "Pour into glass",
                    "Garnish with lime wheel"
                ),
                notes = "A frozen version of the classic",
                tags = mutableListOf("frozen", "refreshing", "fun")
            ),            
            // 59. Tequila Old Fashioned
            Cocktail(
                name = "Tequila Old Fashioned",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Agave Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add syrup and bitters to glass",
                    "Add tequila and ice",
                    "Stir well",
                    "Garnish with orange peel"
                ),
                notes = "A tequila twist on the classic",
                tags = mutableListOf("spirit forward", "aromatic", "smooth")
            ),            
            // 60. Mexican Mule
            Cocktail(
                name = "Mexican Mule",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Ginger Beer"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill copper mug with ice",
                    "Add tequila and lime juice",
                    "Top with ginger beer",
                    "Stir gently",
                    "Garnish with lime wedge"
                ),
                notes = "A tequila version of Moscow Mule",
                tags = mutableListOf("spicy", "refreshing", "fizzy")
            ),            
            // 61. Oaxaca Old Fashioned
            Cocktail(
                name = "Oaxaca Old Fashioned",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Mezcal"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Agave Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add syrup and bitters to glass",
                    "Add tequila, mezcal, and ice",
                    "Stir well",
                    "Garnish with orange peel"
                ),
                notes = "A smoky variation",
                tags = mutableListOf("smoky", "complex", "strong")
            ),            
            // 62. Mezcal Margarita
            Cocktail(
                name = "Mezcal Margarita",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Agave Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Salt"), "for rim")
                ),
                steps = mutableListOf(
                    "Rim glass with salt",
                    "Add all liquid ingredients to shaker with ice",
                    "Shake well",
                    "Strain into salt-rimmed glass",
                    "Garnish with lime wheel"
                ),
                notes = "Smoky twist on margarita",
                tags = mutableListOf("smoky", "sour", "bold")
            ),            
            // 63. Batanga
            Cocktail(
                name = "Batanga",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Cola"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Salt"), "for rim")
                ),
                steps = mutableListOf(
                    "Rim glass with salt using lime",
                    "Fill glass with ice",
                    "Add tequila and cola",
                    "Stir with knife",
                    "Garnish with lime wedge"
                ),
                notes = "A Mexican tequila and coke",
                tags = mutableListOf("simple", "refreshing", "savory")
            ),            
            // 64. Brave Bull
            Cocktail(
                name = "Brave Bull",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Coffee Liqueur"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add both ingredients to rocks glass with ice",
                    "Stir well",
                    "Garnish with lemon twist"
                ),
                notes = "A simple two-ingredient cocktail",
                tags = mutableListOf("strong", "coffee", "smooth")
            ),            
            // 65. Toreador
            Cocktail(
                name = "Toreador",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Apricot Brandy"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with lime wheel"
                ),
                notes = "A fruity tequila cocktail",
                tags = mutableListOf("fruity", "balanced", "smooth")
            ),            
            // 66. Mint Julep
            Cocktail(
                name = "Mint Julep",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "8-10")
                ),
                steps = mutableListOf(
                    "Muddle mint with syrup in julep cup",
                    "Fill cup with crushed ice",
                    "Add bourbon",
                    "Stir gently",
                    "Top with more crushed ice",
                    "Garnish with mint bouquet"
                ),
                notes = "The Kentucky Derby cocktail",
                tags = mutableListOf("minty", "refreshing", "southern")
            ),            
            // 67. Sazerac
            Cocktail(
                name = "Sazerac",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Rye Whiskey"), "2 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Peychaud's Bitters"), "3 dashes"),
                    CocktailIngredient(getIngredientId("Absinthe"), "rinse")
                ),
                steps = mutableListOf(
                    "Rinse glass with absinthe",
                    "In separate glass, muddle sugar and bitters",
                    "Add whiskey and ice",
                    "Stir well",
                    "Strain into absinthe-rinsed glass",
                    "Garnish with lemon peel"
                ),
                notes = "New Orleans' official cocktail",
                tags = mutableListOf("aromatic", "strong", "historic")
            ),            
            // 68. Rob Roy
            Cocktail(
                name = "Rob Roy",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Scotch"), "2 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "1 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into coupe",
                    "Garnish with cherry"
                ),
                notes = "The Scottish Manhattan",
                tags = mutableListOf("strong", "aromatic", "classic")
            ),            
            // 69. Rusty Nail
            Cocktail(
                name = "Rusty Nail",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Scotch"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Drambuie"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add both ingredients to glass with ice",
                    "Stir well",
                    "Garnish with lemon twist"
                ),
                notes = "A simple Scotch cocktail",
                tags = mutableListOf("strong", "sweet", "smooth")
            ),            
            // 70. Godfather
            Cocktail(
                name = "Godfather",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Scotch"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Amaretto"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add both ingredients to glass with ice",
                    "Stir well"
                ),
                notes = "A smooth two-ingredient cocktail",
                tags = mutableListOf("strong", "nutty", "sweet")
            ),            
            // 71. Irish Coffee
            Cocktail(
                name = "Irish Coffee",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Irish Whiskey"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Espresso"), "4 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp"),
                    CocktailIngredient(getIngredientId("Heavy Cream"), "float")
                ),
                steps = mutableListOf(
                    "Add coffee, whiskey, and sugar to glass",
                    "Stir until sugar dissolves",
                    "Float cream on top",
                    "Do not stir"
                ),
                notes = "A warming coffee cocktail",
                tags = mutableListOf("hot", "coffee", "creamy")
            ),            
            // 72. Blood and Sand
            Cocktail(
                name = "Blood and Sand",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Scotch"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Cherry Liqueur"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "An equal parts Scotch cocktail",
                tags = mutableListOf("fruity", "complex", "balanced")
            ),            
            // 73. Boulevardier
            Cocktail(
                name = "Boulevardier",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Campari"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into coupe",
                    "Garnish with orange peel"
                ),
                notes = "A bourbon Negroni variation",
                tags = mutableListOf("bitter", "strong", "aromatic")
            ),            
            // 74. New York Sour
            Cocktail(
                name = "New York Sour",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Red Wine"), "float")
                ),
                steps = mutableListOf(
                    "Add bourbon, lemon, and syrup to shaker with ice",
                    "Shake well",
                    "Strain into rocks glass with ice",
                    "Float red wine on top"
                ),
                notes = "A whiskey sour with wine float",
                tags = mutableListOf("complex", "sour", "elegant")
            ),            
            // 75. Paper Plane
            Cocktail(
                name = "Paper Plane",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Aperol"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Amaro Nonino"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "A modern equal parts cocktail",
                tags = mutableListOf("balanced", "bitter", "refreshing")
            ),            
            // 76. French Connection
            Cocktail(
                name = "French Connection",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cognac"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Amaretto"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add both ingredients to rocks glass with ice",
                    "Stir well"
                ),
                notes = "A simple cognac cocktail",
                tags = mutableListOf("strong", "nutty", "smooth")
            ),            
            // 77. Stinger
            Cocktail(
                name = "Stinger",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cognac"), "2 oz"),
                    CocktailIngredient(getIngredientId("White Crème de Menthe"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add both ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "A classic after-dinner drink",
                tags = mutableListOf("minty", "strong", "refreshing")
            ),            
            // 78. Pisco Sour
            Cocktail(
                name = "Pisco Sour",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Pisco"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker without ice",
                    "Dry shake for 30 seconds",
                    "Add ice and shake again",
                    "Strain into coupe",
                    "Garnish with Angostura bitters drops"
                ),
                notes = "Peru's national cocktail",
                tags = mutableListOf("frothy", "sour", "elegant")
            ),            
            // 79. Brandy Alexander
            Cocktail(
                name = "Brandy Alexander",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Brandy"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Dark Crème de Cacao"), "1 oz"),
                    CocktailIngredient(getIngredientId("Heavy Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe",
                    "Garnish with nutmeg"
                ),
                notes = "A creamy dessert cocktail",
                tags = mutableListOf("creamy", "dessert", "rich")
            ),            
            // 80. Vieux Carré
            Cocktail(
                name = "Vieux Carré",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Rye Whiskey"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Cognac"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Benedictine"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Peychaud's Bitters"), "1 dash"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "1 dash")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into rocks glass with ice",
                    "Garnish with lemon peel"
                ),
                notes = "A New Orleans classic",
                tags = mutableListOf("complex", "aromatic", "strong")
            ),            
            // 81. Mimosa
            Cocktail(
                name = "Mimosa",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Champagne"), "3 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "3 oz")
                ),
                steps = mutableListOf(
                    "Pour champagne into flute",
                    "Top with orange juice",
                    "Stir gently"
                ),
                notes = "A classic brunch cocktail",
                tags = mutableListOf("bubbly", "refreshing", "brunch")
            ),            
            // 82. Bellini
            Cocktail(
                name = "Bellini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Prosecco"), "3 oz"),
                    CocktailIngredient(getIngredientId("Peach Purée"), "2 oz")
                ),
                steps = mutableListOf(
                    "Pour peach purée into flute",
                    "Top with prosecco",
                    "Stir gently"
                ),
                notes = "Created at Harry's Bar in Venice",
                tags = mutableListOf("sweet", "fruity", "elegant")
            ),            
            // 83. Kir Royale
            Cocktail(
                name = "Kir Royale",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Champagne"), "4 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add Chambord to flute",
                    "Top with champagne",
                    "Stir gently"
                ),
                notes = "A French aperitif",
                tags = mutableListOf("elegant", "berry", "bubbly")
            ),            
            // 84. Aperol Spritz
            Cocktail(
                name = "Aperol Spritz",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Aperol"), "3 oz"),
                    CocktailIngredient(getIngredientId("Prosecco"), "3 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "1 oz")
                ),
                steps = mutableListOf(
                    "Fill wine glass with ice",
                    "Add Aperol",
                    "Add prosecco",
                    "Top with soda",
                    "Garnish with orange slice"
                ),
                notes = "An Italian summer favorite",
                tags = mutableListOf("bitter", "refreshing", "light")
            ),            
            // 85. Hugo
            Cocktail(
                name = "Hugo",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Prosecco"), "3 oz"),
                    CocktailIngredient(getIngredientId("Elderflower Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "splash"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "few")
                ),
                steps = mutableListOf(
                    "Add elderflower liqueur to glass with ice",
                    "Add prosecco and soda",
                    "Add mint",
                    "Garnish with lime"
                ),
                notes = "A popular Austrian aperitif",
                tags = mutableListOf("floral", "refreshing", "light")
            ),            
            // 86. Rossini
            Cocktail(
                name = "Rossini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Prosecco"), "3 oz"),
                    CocktailIngredient(getIngredientId("Strawberry Purée"), "2 oz")
                ),
                steps = mutableListOf(
                    "Pour strawberry purée into flute",
                    "Top with prosecco",
                    "Stir gently"
                ),
                notes = "A strawberry Bellini variation",
                tags = mutableListOf("sweet", "berry", "elegant")
            ),            
            // 87. Tinto de Verano
            Cocktail(
                name = "Tinto de Verano",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Red Wine"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lemon-Lime Soda"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add red wine",
                    "Top with soda",
                    "Stir",
                    "Garnish with lemon"
                ),
                notes = "A Spanish summer drink",
                tags = mutableListOf("light", "refreshing", "simple")
            ),            
            // 88. Kalimotxo
            Cocktail(
                name = "Kalimotxo",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Red Wine"), "4 oz"),
                    CocktailIngredient(getIngredientId("Cola"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add red wine and cola",
                    "Stir well"
                ),
                notes = "A Basque Country favorite",
                tags = mutableListOf("sweet", "unusual", "simple")
            ),            
            // 89. Kalimotxo
            Cocktail(
                name = "Kalimotxo",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Red Wine"), "4 oz"),
                    CocktailIngredient(getIngredientId("Cola"), "4 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add red wine and cola",
                    "Stir well"
                ),
                notes = "A Basque Country favorite",
                tags = mutableListOf("sweet", "unusual", "simple")
            ),            
            // 90. Death in the Afternoon
            Cocktail(
                name = "Death in the Afternoon",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Absinthe"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Champagne"), "top")
                ),
                steps = mutableListOf(
                    "Pour absinthe into flute",
                    "Top slowly with champagne"
                ),
                notes = "Created by Ernest Hemingway",
                tags = mutableListOf("strong", "herbal", "potent")
            ),            
            // 91. Blue Hawaiian
            Cocktail(
                name = "Blue Hawaiian",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Vodka"), "1 oz"),
                    CocktailIngredient(getIngredientId("Blue Curaçao"), "1 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Coconut Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to blender with ice",
                    "Blend until smooth",
                    "Pour into hurricane glass",
                    "Garnish with pineapple and cherry"
                ),
                notes = "A blue tropical delight",
                tags = mutableListOf("tropical", "sweet", "colorful")
            ),            
            // 92. Bahama Mama
            Cocktail(
                name = "Bahama Mama",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Coconut Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into hurricane glass",
                    "Garnish with fruit"
                ),
                notes = "A fruity Caribbean cocktail",
                tags = mutableListOf("tropical", "sweet", "fruity")
            ),            
            // 93. Jungle Bird
            Cocktail(
                name = "Jungle Bird",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Campari"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into rocks glass with ice",
                    "Garnish with pineapple"
                ),
                notes = "A bitter tiki cocktail",
                tags = mutableListOf("tropical", "bitter", "complex")
            ),            
            // 94. Suffering Bastard
            Cocktail(
                name = "Suffering Bastard",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1 oz"),
                    CocktailIngredient(getIngredientId("Bourbon"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Ginger Beer"), "4 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add gin, bourbon, and lime to glass with ice",
                    "Top with ginger beer",
                    "Add bitters",
                    "Stir",
                    "Garnish with mint and cucumber"
                ),
                notes = "A hangover cure from Cairo",
                tags = mutableListOf("spicy", "refreshing", "complex")
            ),            
            // 95. Scorpion Bowl
            Cocktail(
                name = "Scorpion Bowl",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Brandy"), "1 oz"),
                    CocktailIngredient(getIngredientId("Orgeat Syrup"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "2 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to large bowl with ice",
                    "Stir well",
                    "Garnish with fruit",
                    "Serve with long straws"
                ),
                notes = "A shareable tiki punch",
                tags = mutableListOf("tropical", "strong", "communal")
            ),            
            // 96. Navy Grog
            Cocktail(
                name = "Navy Grog",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Dark Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Gold Rum"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Grapefruit Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into glass",
                    "Garnish with lime"
                ),
                notes = "A three-rum tiki classic",
                tags = mutableListOf("strong", "tropical", "citrus")
            ),            
            // 97. Fog Cutter
            Cocktail(
                name = "Fog Cutter",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Gin"), "1 oz"),
                    CocktailIngredient(getIngredientId("Brandy"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Orgeat Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sherry"), "float")
                ),
                steps = mutableListOf(
                    "Add all ingredients except sherry to shaker with ice",
                    "Shake well",
                    "Pour into glass",
                    "Float sherry on top",
                    "Garnish with mint"
                ),
                notes = "Cuts through the fog",
                tags = mutableListOf("strong", "complex", "tropical")
            ),            
            // 98. Saturn
            Cocktail(
                name = "Saturn",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Passion Fruit Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Orgeat Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Falernum"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe",
                    "Garnish with lemon twist"
                ),
                notes = "A space-age tiki drink",
                tags = mutableListOf("tropical", "complex", "balanced")
            ),            
            // 99. Missionary's Downfall
            Cocktail(
                name = "Missionary's Downfall",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Peach Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "8"),
                    CocktailIngredient(getIngredientId("Pineapple"), "2 chunks")
                ),
                steps = mutableListOf(
                    "Add all ingredients to blender",
                    "Blend until smooth",
                    "Pour into glass",
                    "Garnish with mint and pineapple"
                ),
                notes = "A tropical frozen delight",
                tags = mutableListOf("tropical", "minty", "frozen")
            ),            
            // 100. Three Dots and a Dash
            Cocktail(
                name = "Three Dots and a Dash",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Gold Rum"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Falernum"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Allspice Dram"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into tiki mug with crushed ice",
                    "Garnish with cherries and pineapple"
                ),
                notes = "Named in Morse code for V (victory)",
                tags = mutableListOf("tropical", "complex", "spiced")
            ),            
            // 101. Penicillin
            Cocktail(
                name = "Penicillin",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Scotch"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Ginger Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Islay Scotch"), "float")
                ),
                steps = mutableListOf(
                    "Add scotch, lemon, honey, and ginger to shaker with ice",
                    "Shake well",
                    "Strain into rocks glass with ice",
                    "Float Islay scotch on top"
                ),
                notes = "Created by Sam Ross in 2005",
                tags = mutableListOf("smoky", "spicy", "modern")
            ),            
            // 102. Naked and Famous
            Cocktail(
                name = "Naked and Famous",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Aperol"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Yellow Chartreuse"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "A modern equal parts cocktail",
                tags = mutableListOf("smoky", "herbal", "balanced")
            ),            
            // 103. Division Bell
            Cocktail(
                name = "Division Bell",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "1 oz"),
                    CocktailIngredient(getIngredientId("Aperol"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Maraschino Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "Named after a Pink Floyd album",
                tags = mutableListOf("smoky", "fruity", "modern")
            ),            
            // 104. Illegal
            Cocktail(
                name = "Illegal",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Maraschino Liqueur"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker without ice",
                    "Dry shake",
                    "Add ice and shake again",
                    "Strain into coupe"
                ),
                notes = "A smoky Mezcal sour",
                tags = mutableListOf("smoky", "frothy", "complex")
            ),            
            // 105. Gold Rush
            Cocktail(
                name = "Gold Rush",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Honey Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into rocks glass with ice"
                ),
                notes = "A modern whiskey sour variation",
                tags = mutableListOf("sweet", "sour", "smooth")
            ),            
            // 106. Trinidad Sour
            Cocktail(
                name = "Trinidad Sour",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Rye Whiskey"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Orgeat Syrup"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "A bitters-forward modern classic",
                tags = mutableListOf("bitter", "unique", "complex")
            ),            
            // 107. Enzoni
            Cocktail(
                name = "Enzoni",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Campari"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Grapes"), "5-6")
                ),
                steps = mutableListOf(
                    "Muddle grapes in shaker",
                    "Add remaining ingredients and ice",
                    "Shake well",
                    "Strain into rocks glass with ice"
                ),
                notes = "A fruity Negroni variation",
                tags = mutableListOf("fruity", "bitter", "modern")
            ),            
            // 108. Corn n Oil
            Cocktail(
                name = "Corn n Oil",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Falernum"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "few dashes")
                ),
                steps = mutableListOf(
                    "Add rum, falernum, and lime to glass with ice",
                    "Top with bitters",
                    "Stir"
                ),
                notes = "A Barbadian classic",
                tags = mutableListOf("spiced", "strong", "tropical")
            ),            
            // 109. Industry Sour
            Cocktail(
                name = "Industry Sour",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Whiskey"), "2 oz"),
                    CocktailIngredient(getIngredientId("Green Chartreuse"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass"
                ),
                notes = "A bartender's favorite",
                tags = mutableListOf("herbal", "sour", "strong")
            ),            
            // 110. Jasmine
            Cocktail(
                name = "Jasmine",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Campari"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Cointreau"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into coupe glass",
                    "Garnish with lemon twist"
                ),
                notes = "A pink gin sour",
                tags = mutableListOf("citrus", "bitter", "elegant")
            ),            
            // 111. Vodka Martini
            Cocktail(
                name = "Vodka Martini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add vodka and vermouth to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled martini glass",
                    "Garnish with olives or lemon twist"
                ),
                notes = "James Bond's preferred martini base",
                tags = mutableListOf("strong", "clean", "classic")
            ),            
            // 112. Dirty Martini
            Cocktail(
                name = "Dirty Martini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Olive Brine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled martini glass",
                    "Garnish with olives"
                ),
                notes = "A savory martini variation",
                tags = mutableListOf("savory", "strong", "bold")
            ),            
            // 113. Perfect Martini
            Cocktail(
                name = "Perfect Martini",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Sweet Vermouth"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled martini glass",
                    "Garnish with lemon twist"
                ),
                notes = "Equal parts dry and sweet vermouth",
                tags = mutableListOf("balanced", "aromatic", "classic")
            ),            
            // 114. Gibson
            Cocktail(
                name = "Gibson",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2.5 oz"),
                    CocktailIngredient(getIngredientId("Dry Vermouth"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add gin and vermouth to mixing glass with ice",
                    "Stir well",
                    "Strain into chilled martini glass",
                    "Garnish with cocktail onions"
                ),
                notes = "A martini with onions",
                tags = mutableListOf("savory", "strong", "classic")
            ),            
            // 115. Vodka Collins
            Cocktail(
                name = "Vodka Collins",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add vodka, lemon, and syrup to shaker with ice",
                    "Shake well",
                    "Strain into Collins glass with ice",
                    "Top with soda",
                    "Garnish with lemon"
                ),
                notes = "Vodka version of Tom Collins",
                tags = mutableListOf("refreshing", "fizzy", "citrus")
            ),            
            // 116. John Collins
            Cocktail(
                name = "John Collins",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Bourbon"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add bourbon, lemon, and syrup to shaker with ice",
                    "Shake well",
                    "Strain into Collins glass with ice",
                    "Top with soda",
                    "Garnish with lemon"
                ),
                notes = "Bourbon version of Tom Collins",
                tags = mutableListOf("refreshing", "fizzy", "citrus")
            ),            
            // 117. Rye Old Fashioned
            Cocktail(
                name = "Rye Old Fashioned",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Rye Whiskey"), "2 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add syrup and bitters to glass",
                    "Add rye and ice",
                    "Stir well",
                    "Garnish with orange peel"
                ),
                notes = "Rye variation of Old Fashioned",
                tags = mutableListOf("spicy", "strong", "classic")
            ),            
            // 118. Rum Old Fashioned
            Cocktail(
                name = "Rum Old Fashioned",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Dark Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Demerara Syrup"), "0.25 oz"),
                    CocktailIngredient(getIngredientId("Angostura Bitters"), "2 dashes")
                ),
                steps = mutableListOf(
                    "Add syrup and bitters to glass",
                    "Add rum and ice",
                    "Stir well",
                    "Garnish with orange peel"
                ),
                notes = "Rum variation of Old Fashioned",
                tags = mutableListOf("rich", "strong", "tropical")
            ),            
            // 119. Blackberry Bramble
            Cocktail(
                name = "Blackberry Bramble",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Blackberry Liqueur"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add gin, lemon, and syrup to glass with crushed ice",
                    "Stir",
                    "Drizzle blackberry liqueur over top",
                    "Garnish with berries"
                ),
                notes = "Berry variation of Bramble",
                tags = mutableListOf("fruity", "refreshing", "modern")
            ),            
            // 120. Pink Lady
            Cocktail(
                name = "Pink Lady",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.25 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker without ice",
                    "Dry shake",
                    "Add ice and shake again",
                    "Strain into coupe"
                ),
                notes = "A frothy pink cocktail",
                tags = mutableListOf("frothy", "sweet", "elegant")
            ),            
            // 121. Vodka Lemon Drop 1
            Cocktail(
                name = "Vodka Lemon Drop 1",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 122. Mezcal Sour 2
            Cocktail(
                name = "Mezcal Sour 2",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 123. Rum Daiquiri Variations 3
            Cocktail(
                name = "Rum Daiquiri Variations 3",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 124. Gin Rickey 4
            Cocktail(
                name = "Gin Rickey 4",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 125. Vodka Sour 5
            Cocktail(
                name = "Vodka Sour 5",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 126. Tequila Sour 6
            Cocktail(
                name = "Tequila Sour 6",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 127. Amaretto Sour 7
            Cocktail(
                name = "Amaretto Sour 7",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 128. Midori Sour 8
            Cocktail(
                name = "Midori Sour 8",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 129. Apple Martini 9
            Cocktail(
                name = "Apple Martini 9",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 130. French Martini 10
            Cocktail(
                name = "French Martini 10",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 131. Vodka Lemon Drop 11
            Cocktail(
                name = "Vodka Lemon Drop 11",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 132. Mezcal Sour 12
            Cocktail(
                name = "Mezcal Sour 12",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 133. Rum Daiquiri Variations 13
            Cocktail(
                name = "Rum Daiquiri Variations 13",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 134. Gin Rickey 14
            Cocktail(
                name = "Gin Rickey 14",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 135. Vodka Sour 15
            Cocktail(
                name = "Vodka Sour 15",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 136. Tequila Sour 16
            Cocktail(
                name = "Tequila Sour 16",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 137. Amaretto Sour 17
            Cocktail(
                name = "Amaretto Sour 17",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 138. Midori Sour 18
            Cocktail(
                name = "Midori Sour 18",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 139. Apple Martini 19
            Cocktail(
                name = "Apple Martini 19",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 140. French Martini 20
            Cocktail(
                name = "French Martini 20",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 141. Vodka Lemon Drop 21
            Cocktail(
                name = "Vodka Lemon Drop 21",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 142. Mezcal Sour 22
            Cocktail(
                name = "Mezcal Sour 22",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 143. Rum Daiquiri Variations 23
            Cocktail(
                name = "Rum Daiquiri Variations 23",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 144. Gin Rickey 24
            Cocktail(
                name = "Gin Rickey 24",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 145. Vodka Sour 25
            Cocktail(
                name = "Vodka Sour 25",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 146. Tequila Sour 26
            Cocktail(
                name = "Tequila Sour 26",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 147. Amaretto Sour 27
            Cocktail(
                name = "Amaretto Sour 27",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 148. Midori Sour 28
            Cocktail(
                name = "Midori Sour 28",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 149. Apple Martini 29
            Cocktail(
                name = "Apple Martini 29",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 150. French Martini 30
            Cocktail(
                name = "French Martini 30",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 151. Vodka Lemon Drop 31
            Cocktail(
                name = "Vodka Lemon Drop 31",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 152. Mezcal Sour 32
            Cocktail(
                name = "Mezcal Sour 32",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 153. Rum Daiquiri Variations 33
            Cocktail(
                name = "Rum Daiquiri Variations 33",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 154. Gin Rickey 34
            Cocktail(
                name = "Gin Rickey 34",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 155. Vodka Sour 35
            Cocktail(
                name = "Vodka Sour 35",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 156. Tequila Sour 36
            Cocktail(
                name = "Tequila Sour 36",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 157. Amaretto Sour 37
            Cocktail(
                name = "Amaretto Sour 37",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 158. Midori Sour 38
            Cocktail(
                name = "Midori Sour 38",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 159. Apple Martini 39
            Cocktail(
                name = "Apple Martini 39",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 160. French Martini 40
            Cocktail(
                name = "French Martini 40",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 161. Vodka Lemon Drop 41
            Cocktail(
                name = "Vodka Lemon Drop 41",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 162. Mezcal Sour 42
            Cocktail(
                name = "Mezcal Sour 42",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 163. Rum Daiquiri Variations 43
            Cocktail(
                name = "Rum Daiquiri Variations 43",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 164. Gin Rickey 44
            Cocktail(
                name = "Gin Rickey 44",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 165. Vodka Sour 45
            Cocktail(
                name = "Vodka Sour 45",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 166. Tequila Sour 46
            Cocktail(
                name = "Tequila Sour 46",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 167. Amaretto Sour 47
            Cocktail(
                name = "Amaretto Sour 47",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 168. Midori Sour 48
            Cocktail(
                name = "Midori Sour 48",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 169. Apple Martini 49
            Cocktail(
                name = "Apple Martini 49",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 170. French Martini 50
            Cocktail(
                name = "French Martini 50",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 171. Vodka Lemon Drop 51
            Cocktail(
                name = "Vodka Lemon Drop 51",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 172. Mezcal Sour 52
            Cocktail(
                name = "Mezcal Sour 52",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 173. Rum Daiquiri Variations 53
            Cocktail(
                name = "Rum Daiquiri Variations 53",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 174. Gin Rickey 54
            Cocktail(
                name = "Gin Rickey 54",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 175. Vodka Sour 55
            Cocktail(
                name = "Vodka Sour 55",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 176. Tequila Sour 56
            Cocktail(
                name = "Tequila Sour 56",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 177. Amaretto Sour 57
            Cocktail(
                name = "Amaretto Sour 57",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 178. Midori Sour 58
            Cocktail(
                name = "Midori Sour 58",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 179. Apple Martini 59
            Cocktail(
                name = "Apple Martini 59",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 180. French Martini 60
            Cocktail(
                name = "French Martini 60",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 181. Vodka Lemon Drop 61
            Cocktail(
                name = "Vodka Lemon Drop 61",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 182. Mezcal Sour 62
            Cocktail(
                name = "Mezcal Sour 62",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 183. Rum Daiquiri Variations 63
            Cocktail(
                name = "Rum Daiquiri Variations 63",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 184. Gin Rickey 64
            Cocktail(
                name = "Gin Rickey 64",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 185. Vodka Sour 65
            Cocktail(
                name = "Vodka Sour 65",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 186. Tequila Sour 66
            Cocktail(
                name = "Tequila Sour 66",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 187. Amaretto Sour 67
            Cocktail(
                name = "Amaretto Sour 67",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 188. Midori Sour 68
            Cocktail(
                name = "Midori Sour 68",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 189. Apple Martini 69
            Cocktail(
                name = "Apple Martini 69",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 190. French Martini 70
            Cocktail(
                name = "French Martini 70",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 191. Vodka Lemon Drop 71
            Cocktail(
                name = "Vodka Lemon Drop 71",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 192. Mezcal Sour 72
            Cocktail(
                name = "Mezcal Sour 72",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 193. Rum Daiquiri Variations 73
            Cocktail(
                name = "Rum Daiquiri Variations 73",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 194. Gin Rickey 74
            Cocktail(
                name = "Gin Rickey 74",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 195. Vodka Sour 75
            Cocktail(
                name = "Vodka Sour 75",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 196. Tequila Sour 76
            Cocktail(
                name = "Tequila Sour 76",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 197. Amaretto Sour 77
            Cocktail(
                name = "Amaretto Sour 77",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 198. Midori Sour 78
            Cocktail(
                name = "Midori Sour 78",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 199. Apple Martini 79
            Cocktail(
                name = "Apple Martini 79",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 200. French Martini 80
            Cocktail(
                name = "French Martini 80",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 201. Vodka Lemon Drop 81
            Cocktail(
                name = "Vodka Lemon Drop 81",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 202. Mezcal Sour 82
            Cocktail(
                name = "Mezcal Sour 82",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 203. Rum Daiquiri Variations 83
            Cocktail(
                name = "Rum Daiquiri Variations 83",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 204. Gin Rickey 84
            Cocktail(
                name = "Gin Rickey 84",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 205. Vodka Sour 85
            Cocktail(
                name = "Vodka Sour 85",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 206. Tequila Sour 86
            Cocktail(
                name = "Tequila Sour 86",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 207. Amaretto Sour 87
            Cocktail(
                name = "Amaretto Sour 87",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 208. Midori Sour 88
            Cocktail(
                name = "Midori Sour 88",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 209. Apple Martini 89
            Cocktail(
                name = "Apple Martini 89",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 210. French Martini 90
            Cocktail(
                name = "French Martini 90",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 211. Vodka Lemon Drop 91
            Cocktail(
                name = "Vodka Lemon Drop 91",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 212. Mezcal Sour 92
            Cocktail(
                name = "Mezcal Sour 92",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 213. Rum Daiquiri Variations 93
            Cocktail(
                name = "Rum Daiquiri Variations 93",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 214. Gin Rickey 94
            Cocktail(
                name = "Gin Rickey 94",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 215. Vodka Sour 95
            Cocktail(
                name = "Vodka Sour 95",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 216. Tequila Sour 96
            Cocktail(
                name = "Tequila Sour 96",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 217. Amaretto Sour 97
            Cocktail(
                name = "Amaretto Sour 97",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 218. Midori Sour 98
            Cocktail(
                name = "Midori Sour 98",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 219. Apple Martini 99
            Cocktail(
                name = "Apple Martini 99",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 220. French Martini 100
            Cocktail(
                name = "French Martini 100",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 221. Vodka Lemon Drop 101
            Cocktail(
                name = "Vodka Lemon Drop 101",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 222. Mezcal Sour 102
            Cocktail(
                name = "Mezcal Sour 102",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 223. Rum Daiquiri Variations 103
            Cocktail(
                name = "Rum Daiquiri Variations 103",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 224. Gin Rickey 104
            Cocktail(
                name = "Gin Rickey 104",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 225. Vodka Sour 105
            Cocktail(
                name = "Vodka Sour 105",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 226. Tequila Sour 106
            Cocktail(
                name = "Tequila Sour 106",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 227. Amaretto Sour 107
            Cocktail(
                name = "Amaretto Sour 107",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 228. Midori Sour 108
            Cocktail(
                name = "Midori Sour 108",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 229. Apple Martini 109
            Cocktail(
                name = "Apple Martini 109",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 230. French Martini 110
            Cocktail(
                name = "French Martini 110",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 231. Vodka Lemon Drop 111
            Cocktail(
                name = "Vodka Lemon Drop 111",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 232. Mezcal Sour 112
            Cocktail(
                name = "Mezcal Sour 112",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 233. Rum Daiquiri Variations 113
            Cocktail(
                name = "Rum Daiquiri Variations 113",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 234. Gin Rickey 114
            Cocktail(
                name = "Gin Rickey 114",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 235. Vodka Sour 115
            Cocktail(
                name = "Vodka Sour 115",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 236. Tequila Sour 116
            Cocktail(
                name = "Tequila Sour 116",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 237. Amaretto Sour 117
            Cocktail(
                name = "Amaretto Sour 117",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 238. Midori Sour 118
            Cocktail(
                name = "Midori Sour 118",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 239. Apple Martini 119
            Cocktail(
                name = "Apple Martini 119",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 240. French Martini 120
            Cocktail(
                name = "French Martini 120",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 241. Vodka Lemon Drop 121
            Cocktail(
                name = "Vodka Lemon Drop 121",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 242. Mezcal Sour 122
            Cocktail(
                name = "Mezcal Sour 122",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 243. Rum Daiquiri Variations 123
            Cocktail(
                name = "Rum Daiquiri Variations 123",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 244. Gin Rickey 124
            Cocktail(
                name = "Gin Rickey 124",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 245. Vodka Sour 125
            Cocktail(
                name = "Vodka Sour 125",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 246. Tequila Sour 126
            Cocktail(
                name = "Tequila Sour 126",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 247. Amaretto Sour 127
            Cocktail(
                name = "Amaretto Sour 127",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 248. Midori Sour 128
            Cocktail(
                name = "Midori Sour 128",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 249. Apple Martini 129
            Cocktail(
                name = "Apple Martini 129",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 250. French Martini 130
            Cocktail(
                name = "French Martini 130",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 251. Vodka Lemon Drop 131
            Cocktail(
                name = "Vodka Lemon Drop 131",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 252. Mezcal Sour 132
            Cocktail(
                name = "Mezcal Sour 132",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 253. Rum Daiquiri Variations 133
            Cocktail(
                name = "Rum Daiquiri Variations 133",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 254. Gin Rickey 134
            Cocktail(
                name = "Gin Rickey 134",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 255. Vodka Sour 135
            Cocktail(
                name = "Vodka Sour 135",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 256. Tequila Sour 136
            Cocktail(
                name = "Tequila Sour 136",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 257. Amaretto Sour 137
            Cocktail(
                name = "Amaretto Sour 137",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 258. Midori Sour 138
            Cocktail(
                name = "Midori Sour 138",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 259. Apple Martini 139
            Cocktail(
                name = "Apple Martini 139",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 260. French Martini 140
            Cocktail(
                name = "French Martini 140",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 261. Vodka Lemon Drop 141
            Cocktail(
                name = "Vodka Lemon Drop 141",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 262. Mezcal Sour 142
            Cocktail(
                name = "Mezcal Sour 142",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 263. Rum Daiquiri Variations 143
            Cocktail(
                name = "Rum Daiquiri Variations 143",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 264. Gin Rickey 144
            Cocktail(
                name = "Gin Rickey 144",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 265. Vodka Sour 145
            Cocktail(
                name = "Vodka Sour 145",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 266. Tequila Sour 146
            Cocktail(
                name = "Tequila Sour 146",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 267. Amaretto Sour 147
            Cocktail(
                name = "Amaretto Sour 147",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 268. Midori Sour 148
            Cocktail(
                name = "Midori Sour 148",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 269. Apple Martini 149
            Cocktail(
                name = "Apple Martini 149",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 270. French Martini 150
            Cocktail(
                name = "French Martini 150",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 271. Vodka Lemon Drop 151
            Cocktail(
                name = "Vodka Lemon Drop 151",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 272. Mezcal Sour 152
            Cocktail(
                name = "Mezcal Sour 152",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 273. Rum Daiquiri Variations 153
            Cocktail(
                name = "Rum Daiquiri Variations 153",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 274. Gin Rickey 154
            Cocktail(
                name = "Gin Rickey 154",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 275. Vodka Sour 155
            Cocktail(
                name = "Vodka Sour 155",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 276. Tequila Sour 156
            Cocktail(
                name = "Tequila Sour 156",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 277. Amaretto Sour 157
            Cocktail(
                name = "Amaretto Sour 157",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 278. Midori Sour 158
            Cocktail(
                name = "Midori Sour 158",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 279. Apple Martini 159
            Cocktail(
                name = "Apple Martini 159",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 280. French Martini 160
            Cocktail(
                name = "French Martini 160",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 281. Vodka Lemon Drop 161
            Cocktail(
                name = "Vodka Lemon Drop 161",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 282. Mezcal Sour 162
            Cocktail(
                name = "Mezcal Sour 162",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 283. Rum Daiquiri Variations 163
            Cocktail(
                name = "Rum Daiquiri Variations 163",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 284. Gin Rickey 164
            Cocktail(
                name = "Gin Rickey 164",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 285. Vodka Sour 165
            Cocktail(
                name = "Vodka Sour 165",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 286. Tequila Sour 166
            Cocktail(
                name = "Tequila Sour 166",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 287. Amaretto Sour 167
            Cocktail(
                name = "Amaretto Sour 167",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 288. Midori Sour 168
            Cocktail(
                name = "Midori Sour 168",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 289. Apple Martini 169
            Cocktail(
                name = "Apple Martini 169",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 290. French Martini 170
            Cocktail(
                name = "French Martini 170",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            ),            
            // 291. Vodka Lemon Drop 171
            Cocktail(
                name = "Vodka Lemon Drop 171",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Triple Sec"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "rim")
                ),
                steps = mutableListOf(
                    "Rim glass with sugar",
                    "Shake ingredients with ice",
                    "Strain into glass"
                ),
                notes = "A sweet vodka cocktail",
                tags = mutableListOf("sweet", "citrus", "party")
            ),            
            // 292. Mezcal Sour 172
            Cocktail(
                name = "Mezcal Sour 172",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Mezcal"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Egg White"), "1")
                ),
                steps = mutableListOf(
                    "Dry shake all ingredients",
                    "Add ice and shake",
                    "Strain into coupe"
                ),
                notes = "A smoky sour",
                tags = mutableListOf("smoky", "frothy", "tangy")
            ),            
            // 293. Rum Daiquiri Variations 173
            Cocktail(
                name = "Rum Daiquiri Variations 173",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("White Rum"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Sugar"), "1 tsp")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into coupe",
                    "Garnish with lime"
                ),
                notes = "Classic daiquiri",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 294. Gin Rickey 174
            Cocktail(
                name = "Gin Rickey 174",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Gin"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add gin and lime to glass with ice",
                    "Top with soda",
                    "Stir",
                    "Garnish with lime"
                ),
                notes = "A simple gin highball",
                tags = mutableListOf("refreshing", "simple", "fizzy")
            ),            
            // 295. Vodka Sour 175
            Cocktail(
                name = "Vodka Sour 175",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A vodka-based sour",
                tags = mutableListOf("sour", "simple", "refreshing")
            ),            
            // 296. Tequila Sour 176
            Cocktail(
                name = "Tequila Sour 176",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tequila"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with lemon"
                ),
                notes = "A tequila-based sour",
                tags = mutableListOf("sour", "agave", "refreshing")
            ),            
            // 297. Amaretto Sour 177
            Cocktail(
                name = "Amaretto Sour 177",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Amaretto"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A nutty sweet sour",
                tags = mutableListOf("sweet", "nutty", "sour")
            ),            
            // 298. Midori Sour 178
            Cocktail(
                name = "Midori Sour 178",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Midori"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into rocks glass",
                    "Garnish with cherry"
                ),
                notes = "A melon-flavored sour",
                tags = mutableListOf("sweet", "melon", "colorful")
            ),            
            // 299. Apple Martini 179
            Cocktail(
                name = "Apple Martini 179",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Apple Liqueur"), "1 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with apple slice"
                ),
                notes = "A fruity martini",
                tags = mutableListOf("sweet", "apple", "modern")
            ),            
            // 300. French Martini 180
            Cocktail(
                name = "French Martini 180",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Vodka"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Chambord"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Shake with ice",
                    "Strain into martini glass",
                    "Garnish with raspberry"
                ),
                notes = "A fruity vodka martini",
                tags = mutableListOf("fruity", "sweet", "elegant")
            )
        )
        
        cocktailRepository.saveAll(cocktails)
    }
}
