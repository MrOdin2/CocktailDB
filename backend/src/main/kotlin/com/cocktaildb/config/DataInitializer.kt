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
                tags = mutableListOf("sour", "refreshing", "classic"),
                abv = 27,
                baseSpirit = "tequila"
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
                tags = mutableListOf("refreshing", "sweet", "minty"),
                abv = 13,
                baseSpirit = "rum"
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
                tags = mutableListOf("spirit forward", "classic", "strong"),
                abv = 32,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("sweet", "fruity", "modern"),
                abv = 18,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sour", "refreshing", "classic"),
                abv = 20,
                baseSpirit = "rum"
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
                tags = mutableListOf("refreshing", "spicy", "fizzy"),
                abv = 11,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sweet", "creamy", "tropical"),
                abv = 12,
                baseSpirit = "rum"
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
                tags = mutableListOf("refreshing", "fizzy", "simple"),
                abv = 11,
                baseSpirit = "gin"
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
                tags = mutableListOf("tropical", "sweet", "complex"),
                abv = 17,
                baseSpirit = "rum"
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
                tags = mutableListOf("sour", "classic", "balanced"),
                abv = 18,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("sweet", "simple", "fizzy"),
                abv = 12,
                baseSpirit = "rum"
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
                tags = mutableListOf("creamy", "sweet", "dessert"),
                abv = 16,
                baseSpirit = "vodka"
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
                tags = mutableListOf("bitter", "spirit forward", "classic"),
                abv = 24,
                baseSpirit = "gin"
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
                tags = mutableListOf("spirit forward", "classic", "elegant"),
                abv = 28,
                baseSpirit = "gin"
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
                tags = mutableListOf("spirit forward", "classic", "strong"),
                abv = 28,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("sour", "classic", "elegant"),
                abv = 22,
                baseSpirit = "cognac"
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
                tags = mutableListOf("refreshing", "fizzy", "sour"),
                abv = 12,
                baseSpirit = "gin"
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
                tags = mutableListOf("simple", "fruity", "sweet"),
                abv = 12,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sweet", "fruity", "colorful"),
                abv = 14,
                baseSpirit = "tequila"
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
                tags = mutableListOf("refreshing", "spicy", "simple"),
                abv = 12,
                baseSpirit = "rum"
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
                tags = mutableListOf("savory", "brunch", "spicy"),
                abv = 12,
                baseSpirit = "vodka"
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
                tags = mutableListOf("coffee", "dessert", "energizing"),
                abv = 15,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sour", "simple", "classic"),
                abv = 22,
                baseSpirit = "vodka"
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
                tags = mutableListOf("fruity", "refreshing", "simple"),
                abv = 9,
                baseSpirit = "vodka"
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
                tags = mutableListOf("fruity", "tropical", "simple"),
                abv = 9,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sweet", "sour", "citrus"),
                abv = 18,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sweet", "fruity", "modern"),
                abv = 16,
                baseSpirit = "vodka"
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
                tags = mutableListOf("tropical", "creamy", "sweet"),
                abv = 12,
                baseSpirit = "vodka"
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
                tags = mutableListOf("sweet", "fruity", "retro"),
                abv = 12,
                baseSpirit = "vodka"
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
                tags = mutableListOf("strong", "citrus", "sharp"),
                abv = 22,
                baseSpirit = "vodka"
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
                tags = mutableListOf("simple", "refreshing", "fizzy"),
                abv = 12,
                baseSpirit = "vodka"
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
                tags = mutableListOf("fruity", "simple", "tart"),
                abv = 9,
                baseSpirit = "vodka"
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
                tags = mutableListOf("fruity", "refreshing", "simple"),
                abv = 10,
                baseSpirit = "vodka"
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
                tags = mutableListOf("strong", "classic", "elegant"),
                abv = 24,
                baseSpirit = "vodka"
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
                tags = mutableListOf("strong", "coffee", "simple"),
                abv = 24,
                baseSpirit = "vodka"
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
                tags = mutableListOf("floral", "sour", "elegant"),
                abv = 20,
                baseSpirit = "gin"
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
                tags = mutableListOf("sweet", "sour", "balanced"),
                abv = 18,
                baseSpirit = "gin"
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
                tags = mutableListOf("fruity", "refreshing", "modern"),
                abv = 14,
                baseSpirit = "gin"
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
                tags = mutableListOf("complex", "herbal", "strong"),
                abv = 22,
                baseSpirit = "gin"
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
                tags = mutableListOf("elegant", "bubbly", "refreshing"),
                abv = 15,
                baseSpirit = "gin"
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
                tags = mutableListOf("sour", "simple", "classic"),
                abv = 22,
                baseSpirit = "gin"
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
                tags = mutableListOf("herbal", "complex", "balanced"),
                abv = 25,
                baseSpirit = "gin"
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
                tags = mutableListOf("minty", "refreshing", "herbal"),
                abv = 16,
                baseSpirit = "gin"
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
                tags = mutableListOf("tropical", "complex", "sweet"),
                abv = 14,
                baseSpirit = "gin"
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
                tags = mutableListOf("frothy", "sour", "elegant"),
                abv = 15,
                baseSpirit = "gin"
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
                tags = mutableListOf("refreshing", "simple", "tangy"),
                abv = 18,
                baseSpirit = "cachaça"
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
                tags = mutableListOf("tropical", "fruity", "strong"),
                abv = 20,
                baseSpirit = "rum"
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
                tags = mutableListOf("tropical", "creamy", "sweet"),
                abv = 13,
                baseSpirit = "rum"
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
                tags = mutableListOf("balanced", "refreshing", "classic"),
                abv = 15,
                baseSpirit = "rum"
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
                tags = mutableListOf("strong", "tropical", "dangerous"),
                abv = 22,
                baseSpirit = "rum"
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
                tags = mutableListOf("strong", "citrus", "elegant"),
                abv = 24,
                baseSpirit = "rum"
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
                tags = mutableListOf("elegant", "balanced", "aromatic"),
                abv = 23,
                baseSpirit = "rum"
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
                tags = mutableListOf("sweet", "fruity", "classic"),
                abv = 15,
                baseSpirit = "rum"
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
                tags = mutableListOf("hot", "comforting", "spiced"),
                abv = 14,
                baseSpirit = "rum"
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
                tags = mutableListOf("tropical", "fruity", "sweet"),
                abv = 13,
                baseSpirit = "rum"
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
                tags = mutableListOf("refreshing", "citrus", "classic"),
                abv = 13,
                baseSpirit = "tequila"
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
                tags = mutableListOf("sour", "simple", "agave"),
                abv = 28,
                baseSpirit = "tequila"
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
                tags = mutableListOf("frozen", "refreshing", "fun"),
                abv = 20,
                baseSpirit = "tequila"
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
                tags = mutableListOf("spirit forward", "aromatic", "smooth"),
                abv = 32,
                baseSpirit = "tequila"
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
                tags = mutableListOf("spicy", "refreshing", "fizzy"),
                abv = 12,
                baseSpirit = "tequila"
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
                tags = mutableListOf("smoky", "complex", "strong"),
                abv = 30,
                baseSpirit = "mezcal"
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
                tags = mutableListOf("smoky", "sour", "bold"),
                abv = 20,
                baseSpirit = "mezcal"
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
                tags = mutableListOf("simple", "refreshing", "savory"),
                abv = 12,
                baseSpirit = "tequila"
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
                tags = mutableListOf("strong", "coffee", "smooth"),
                abv = 25,
                baseSpirit = "tequila"
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
                tags = mutableListOf("fruity", "balanced", "smooth"),
                abv = 24,
                baseSpirit = "tequila"
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
                tags = mutableListOf("minty", "refreshing", "southern"),
                abv = 22,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("aromatic", "strong", "historic"),
                abv = 31,
                baseSpirit = "rye"
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
                tags = mutableListOf("strong", "aromatic", "classic"),
                abv = 28,
                baseSpirit = "scotch"
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
                tags = mutableListOf("strong", "sweet", "smooth"),
                abv = 28,
                baseSpirit = "scotch"
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
                tags = mutableListOf("strong", "nutty", "sweet"),
                abv = 26,
                baseSpirit = "whiskey"
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
                tags = mutableListOf("hot", "coffee", "creamy"),
                abv = 8,
                baseSpirit = "whiskey"
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
                tags = mutableListOf("fruity", "complex", "balanced"),
                abv = 18,
                baseSpirit = "scotch"
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
                tags = mutableListOf("bitter", "strong", "aromatic"),
                abv = 26,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("complex", "sour", "elegant"),
                abv = 16,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("balanced", "bitter", "refreshing"),
                abv = 22,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("strong", "nutty", "smooth"),
                abv = 26,
                baseSpirit = "cognac"
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
                tags = mutableListOf("minty", "strong", "refreshing"),
                abv = 24,
                baseSpirit = "cognac"
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
                tags = mutableListOf("frothy", "sour", "elegant"),
                abv = 16,
                baseSpirit = "pisco"
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
                tags = mutableListOf("creamy", "dessert", "rich"),
                abv = 16,
                baseSpirit = "brandy"
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
                tags = mutableListOf("complex", "aromatic", "strong"),
                abv = 27,
                baseSpirit = "rye"
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
                tags = mutableListOf("bubbly", "refreshing", "brunch"),
                abv = 6,
                baseSpirit = "champagne"
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
                tags = mutableListOf("sweet", "fruity", "elegant"),
                abv = 6,
                baseSpirit = "prosecco"
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
                tags = mutableListOf("elegant", "berry", "bubbly"),
                abv = 9,
                baseSpirit = "champagne"
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
                tags = mutableListOf("bitter", "refreshing", "light"),
                abv = 8,
                baseSpirit = "prosecco"
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
                tags = mutableListOf("floral", "refreshing", "light"),
                abv = 7,
                baseSpirit = "prosecco"
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
                tags = mutableListOf("sweet", "berry", "elegant"),
                abv = 6,
                baseSpirit = "prosecco"
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
                tags = mutableListOf("light", "refreshing", "simple"),
                abv = 6,
                baseSpirit = "wine"
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
                tags = mutableListOf("sweet", "unusual", "simple"),
                abv = 6,
                baseSpirit = "wine"
            ),            
            // 89. Sherry Cobbler
            Cocktail(
                name = "Sherry Cobbler",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Sherry"), "4 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Orange Slice"), "2")
                ),
                steps = mutableListOf(
                    "Muddle orange slices with syrup",
                    "Add sherry and fill glass with crushed ice",
                    "Stir well",
                    "Garnish with berries and mint"
                ),
                notes = "A refreshing sherry drink",
                tags = mutableListOf("refreshing", "fruity", "classic"),
                abv = 12,
                baseSpirit = "sherry"
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
                tags = mutableListOf("strong", "herbal", "potent"),
                abv = 18,
                baseSpirit = "absinthe"
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
                tags = mutableListOf("tropical", "sweet", "colorful"),
                abv = 13,
                baseSpirit = "rum"
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
                tags = mutableListOf("tropical", "sweet", "fruity"),
                abv = 14,
                baseSpirit = "rum"
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
                tags = mutableListOf("tropical", "bitter", "complex"),
                abv = 15,
                baseSpirit = "rum"
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
                tags = mutableListOf("spicy", "refreshing", "complex"),
                abv = 13,
                baseSpirit = "gin"
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
                tags = mutableListOf("tropical", "strong", "communal"),
                abv = 16,
                baseSpirit = "rum"
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
                tags = mutableListOf("strong", "tropical", "citrus"),
                abv = 20,
                baseSpirit = "rum"
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
                tags = mutableListOf("strong", "complex", "tropical"),
                abv = 18,
                baseSpirit = "rum"
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
                tags = mutableListOf("tropical", "complex", "balanced"),
                abv = 16,
                baseSpirit = "gin"
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
                tags = mutableListOf("tropical", "minty", "frozen"),
                abv = 14,
                baseSpirit = "rum"
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
                tags = mutableListOf("tropical", "complex", "spiced"),
                abv = 17,
                baseSpirit = "rum"
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
                tags = mutableListOf("smoky", "spicy", "modern"),
                abv = 17,
                baseSpirit = "scotch"
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
                tags = mutableListOf("smoky", "herbal", "balanced"),
                abv = 22,
                baseSpirit = "mezcal"
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
                tags = mutableListOf("smoky", "fruity", "modern"),
                abv = 19,
                baseSpirit = "mezcal"
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
                tags = mutableListOf("smoky", "frothy", "complex"),
                abv = 17,
                baseSpirit = "mezcal"
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
                tags = mutableListOf("sweet", "sour", "smooth"),
                abv = 18,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("bitter", "unique", "complex"),
                abv = 20,
                baseSpirit = "rye"
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
                tags = mutableListOf("fruity", "bitter", "modern"),
                abv = 16,
                baseSpirit = "gin"
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
                tags = mutableListOf("spiced", "strong", "tropical"),
                abv = 18,
                baseSpirit = "rum"
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
                tags = mutableListOf("herbal", "sour", "strong"),
                abv = 18,
                baseSpirit = "whiskey"
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
                tags = mutableListOf("citrus", "bitter", "elegant"),
                abv = 20,
                baseSpirit = "gin"
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
                tags = mutableListOf("strong", "clean", "classic"),
                abv = 27,
                baseSpirit = "vodka"
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
                tags = mutableListOf("savory", "strong", "bold"),
                abv = 26,
                baseSpirit = "vodka"
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
                tags = mutableListOf("balanced", "aromatic", "classic"),
                abv = 27,
                baseSpirit = "gin"
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
                tags = mutableListOf("savory", "strong", "classic"),
                abv = 28,
                baseSpirit = "gin"
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
                tags = mutableListOf("refreshing", "fizzy", "citrus"),
                abv = 12,
                baseSpirit = "vodka"
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
                tags = mutableListOf("refreshing", "fizzy", "citrus"),
                abv = 12,
                baseSpirit = "bourbon"
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
                tags = mutableListOf("spicy", "strong", "classic"),
                abv = 32,
                baseSpirit = "rye"
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
                tags = mutableListOf("rich", "strong", "tropical"),
                abv = 32,
                baseSpirit = "rum"
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
                tags = mutableListOf("fruity", "refreshing", "modern"),
                abv = 14,
                baseSpirit = "gin"
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
                tags = mutableListOf("frothy", "sweet", "elegant"),
                abv = 15,
                baseSpirit = "gin"
            ),
            
            // ALCOHOL-FREE COCKTAILS / MOCKTAILS
            
            // 121. Virgin Mojito
            Cocktail(
                name = "Virgin Mojito",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "10-12 leaves"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Muddle mint leaves with simple syrup and lime juice in glass",
                    "Fill glass with ice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with mint sprig and lime wedge"
                ),
                notes = "Refreshing non-alcoholic version of the classic Mojito",
                tags = mutableListOf("refreshing", "minty", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 122. Virgin Piña Colada
            Cocktail(
                name = "Virgin Piña Colada",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Coconut Cream"), "2 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to blender with ice",
                    "Blend until smooth",
                    "Pour into hurricane glass",
                    "Garnish with pineapple wedge and cherry"
                ),
                notes = "A tropical delight without the rum",
                tags = mutableListOf("tropical", "creamy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 123. Shirley Temple
            Cocktail(
                name = "Shirley Temple",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Ginger Ale"), "6 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add ginger ale",
                    "Add grenadine",
                    "Stir gently",
                    "Garnish with cherry and orange slice"
                ),
                notes = "A classic kid-friendly mocktail",
                tags = mutableListOf("sweet", "fizzy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 124. Roy Rogers
            Cocktail(
                name = "Roy Rogers",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cola"), "6 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add cola",
                    "Add grenadine",
                    "Stir gently",
                    "Garnish with cherry"
                ),
                notes = "The cola version of Shirley Temple",
                tags = mutableListOf("sweet", "fizzy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 125. Virgin Mary
            Cocktail(
                name = "Virgin Mary",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Tomato Juice"), "6 oz"),
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
                notes = "All the flavor of a Bloody Mary without the vodka",
                tags = mutableListOf("savory", "spicy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 126. Cinderella
            Cocktail(
                name = "Cinderella",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into glass with ice",
                    "Garnish with orange slice and cherry"
                ),
                notes = "A fruity and refreshing mocktail",
                tags = mutableListOf("fruity", "sweet", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 127. Arnold Palmer
            Cocktail(
                name = "Arnold Palmer",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lemon Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "1 oz"),
                    CocktailIngredient(getIngredientId("Water"), "2 oz")
                ),
                steps = mutableListOf(
                    "Mix lemon juice, simple syrup, and water in glass",
                    "Add iced tea (4 oz)",
                    "Fill glass with ice",
                    "Stir well",
                    "Garnish with lemon wedge"
                ),
                notes = "Half lemonade, half iced tea - named after the golf legend",
                tags = mutableListOf("refreshing", "citrus", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 128. Virgin Daiquiri
            Cocktail(
                name = "Virgin Daiquiri",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lime Juice"), "1.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add lime juice and simple syrup to shaker with ice",
                    "Shake well",
                    "Strain into chilled coupe glass",
                    "Garnish with lime wheel"
                ),
                notes = "Crisp and tart lime refreshment",
                tags = mutableListOf("tart", "refreshing", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 129. Sunrise Mocktail
            Cocktail(
                name = "Sunrise Mocktail",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Orange Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add orange juice",
                    "Slowly pour grenadine to create sunrise effect",
                    "Garnish with orange slice and cherry"
                ),
                notes = "Beautiful gradient effect without tequila",
                tags = mutableListOf("sweet", "fruity", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 130. Lemon Lime Bitters
            Cocktail(
                name = "Lemon Lime Bitters",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Lemon-Lime Soda"), "6 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add lemon and lime juice",
                    "Top with lemon-lime soda",
                    "Stir gently",
                    "Garnish with lemon and lime wedges"
                ),
                notes = "Light and refreshing citrus drink",
                tags = mutableListOf("citrus", "refreshing", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 131. Virgin Moscow Mule
            Cocktail(
                name = "Virgin Moscow Mule",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Ginger Beer"), "6 oz")
                ),
                steps = mutableListOf(
                    "Fill copper mug with ice",
                    "Add lime juice",
                    "Top with ginger beer",
                    "Stir gently",
                    "Garnish with lime wedge and mint"
                ),
                notes = "Spicy ginger kick without the vodka",
                tags = mutableListOf("spicy", "refreshing", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 132. Tropical Punch
            Cocktail(
                name = "Tropical Punch",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Grenadine"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Pour into glass",
                    "Garnish with pineapple and cherry"
                ),
                notes = "A fruity tropical paradise",
                tags = mutableListOf("tropical", "fruity", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 133. Cucumber Mint Cooler
            Cocktail(
                name = "Cucumber Mint Cooler",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cucumber"), "4 slices"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "8 leaves"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Muddle cucumber and mint with lime juice and syrup",
                    "Fill glass with ice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with cucumber slice and mint"
                ),
                notes = "Cool and refreshing garden flavors",
                tags = mutableListOf("refreshing", "herbal", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 134. Strawberry Lemonade
            Cocktail(
                name = "Strawberry Lemonade",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Strawberry Purée"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "1 oz"),
                    CocktailIngredient(getIngredientId("Water"), "2 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into glass with ice",
                    "Garnish with strawberry and lemon"
                ),
                notes = "Sweet berries meet tangy lemon",
                tags = mutableListOf("fruity", "sweet", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 135. Watermelon Cooler
            Cocktail(
                name = "Watermelon Cooler",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Watermelon Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "2 oz")
                ),
                steps = mutableListOf(
                    "Add watermelon juice, lime juice, and syrup to glass with ice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with watermelon wedge"
                ),
                notes = "Summer in a glass",
                tags = mutableListOf("fruity", "refreshing", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 136. Citrus Sparkler
            Cocktail(
                name = "Citrus Sparkler",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Grapefruit Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add citrus juices to glass with ice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with citrus wheel"
                ),
                notes = "Bright and bubbly citrus medley",
                tags = mutableListOf("citrus", "fizzy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 137. Ginger Lemonade
            Cocktail(
                name = "Ginger Lemonade",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lemon Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Ginger Syrup"), "1 oz"),
                    CocktailIngredient(getIngredientId("Water"), "3 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to glass with ice",
                    "Stir well",
                    "Garnish with lemon wedge and candied ginger"
                ),
                notes = "Zesty lemon with a spicy ginger kick",
                tags = mutableListOf("spicy", "citrus", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 138. Virgin Pina
            Cocktail(
                name = "Virgin Pina",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Add all ingredients to shaker with ice",
                    "Shake well",
                    "Strain into glass with ice",
                    "Garnish with pineapple wedge"
                ),
                notes = "Tropical pineapple refreshment",
                tags = mutableListOf("tropical", "tangy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 139. Basil Lemonade
            Cocktail(
                name = "Basil Lemonade",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Lemon Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "1 oz"),
                    CocktailIngredient(getIngredientId("Basil"), "6-8 leaves"),
                    CocktailIngredient(getIngredientId("Water"), "3 oz")
                ),
                steps = mutableListOf(
                    "Muddle basil with simple syrup",
                    "Add lemon juice and water",
                    "Fill glass with ice",
                    "Stir well",
                    "Garnish with basil sprig"
                ),
                notes = "Herbal twist on classic lemonade",
                tags = mutableListOf("herbal", "refreshing", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 140. Cranberry Spritzer
            Cocktail(
                name = "Cranberry Spritzer",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Cranberry Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "3 oz"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add cranberry juice and lime juice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with lime wheel"
                ),
                notes = "Light and bubbly cranberry drink",
                tags = mutableListOf("tart", "fizzy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 141. Apple Ginger Fizz
            Cocktail(
                name = "Apple Ginger Fizz",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Apple Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Ginger Beer"), "2 oz")
                ),
                steps = mutableListOf(
                    "Fill glass with ice",
                    "Add apple juice and lemon juice",
                    "Top with ginger beer",
                    "Stir gently",
                    "Garnish with apple slice"
                ),
                notes = "Crisp apple with spicy ginger",
                tags = mutableListOf("fruity", "spicy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 142. Pineapple Mint Cooler
            Cocktail(
                name = "Pineapple Mint Cooler",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Pineapple Juice"), "4 oz"),
                    CocktailIngredient(getIngredientId("Mint Leaves"), "8 leaves"),
                    CocktailIngredient(getIngredientId("Lime Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "2 oz")
                ),
                steps = mutableListOf(
                    "Muddle mint with lime juice",
                    "Add pineapple juice",
                    "Fill glass with ice",
                    "Top with club soda",
                    "Garnish with mint and pineapple"
                ),
                notes = "Tropical pineapple with fresh mint",
                tags = mutableListOf("tropical", "minty", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 143. Orange Cream Soda
            Cocktail(
                name = "Orange Cream Soda",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Orange Juice"), "2 oz"),
                    CocktailIngredient(getIngredientId("Vanilla Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "4 oz"),
                    CocktailIngredient(getIngredientId("Heavy Cream"), "1 oz")
                ),
                steps = mutableListOf(
                    "Add orange juice, vanilla syrup, and cream to glass",
                    "Fill with ice",
                    "Top with club soda",
                    "Stir gently",
                    "Garnish with orange slice"
                ),
                notes = "Creamy dreamsicle in a glass",
                tags = mutableListOf("creamy", "sweet", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 144. Berry Fizz
            Cocktail(
                name = "Berry Fizz",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Strawberry Purée"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Club Soda"), "top")
                ),
                steps = mutableListOf(
                    "Add strawberry purée, lemon juice, and syrup to shaker with ice",
                    "Shake well",
                    "Strain into glass with ice",
                    "Top with club soda",
                    "Garnish with fresh berries"
                ),
                notes = "Bubbly berry refreshment",
                tags = mutableListOf("fruity", "fizzy", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 145. Peach Iced Tea
            Cocktail(
                name = "Peach Iced Tea",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Peach Purée"), "2 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "1 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Water"), "3 oz")
                ),
                steps = mutableListOf(
                    "Mix all ingredients in glass",
                    "Add iced tea",
                    "Fill with ice",
                    "Stir well",
                    "Garnish with peach slice"
                ),
                notes = "Sweet peach meets refreshing tea",
                tags = mutableListOf("fruity", "refreshing", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
            
            // 146. Rosemary Citrus Cooler
            Cocktail(
                name = "Rosemary Citrus Cooler",
                ingredients = mutableListOf(
                    CocktailIngredient(getIngredientId("Grapefruit Juice"), "3 oz"),
                    CocktailIngredient(getIngredientId("Lemon Juice"), "0.5 oz"),
                    CocktailIngredient(getIngredientId("Simple Syrup"), "0.75 oz"),
                    CocktailIngredient(getIngredientId("Rosemary"), "1 sprig"),
                    CocktailIngredient(getIngredientId("Club Soda"), "2 oz")
                ),
                steps = mutableListOf(
                    "Muddle rosemary with simple syrup",
                    "Add grapefruit and lemon juice",
                    "Fill glass with ice",
                    "Top with club soda",
                    "Garnish with rosemary sprig"
                ),
                notes = "Herbaceous and citrusy",
                tags = mutableListOf("herbal", "citrus", "non-alcoholic"),
                abv = 0,
                baseSpirit = "none"
            ),
        )
        
        cocktailRepository.saveAll(cocktails)
    }
}
