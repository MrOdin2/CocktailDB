# Non-Volume Measures Guide

## Overview

CocktailDB supports both **volume-based measures** (e.g., 60 ml of vodka, 2 oz of lime juice) and **count-based measures** (e.g., 3 coffee beans, 8 mint leaves) for cocktail ingredients.

This feature allows you to accurately represent ingredients that are measured by count rather than volume, such as garnishes and other non-liquid ingredients.

## How It Works

### Storage Convention

The system uses a clever convention to store both types of measures in the same database field (`measureMl`):

- **Positive values** represent volume measures in milliliters
  - Example: `60.0` = 60 ml
  - Displayed based on user's preferred unit (ml, oz, or cl)

- **Negative values** represent count-based measures (item counts)
  - Example: `-3.0` = 3 items
  - Example: `-8.0` = 8 items
  - Displayed as the absolute value without a unit (e.g., "3" or "8")

This approach requires **no database schema changes** and maintains backward compatibility.

### Automatic Type Detection

The frontend automatically detects whether an ingredient should use count-based or volume-based input:

- **Count-based ingredients** (use negative values):
  - `GARNISH` type (mint leaves, coffee beans, lime wedges, etc.)
  - `OTHER` type (eggs, dashes of bitters, etc.)

- **Volume-based ingredients** (use positive values):
  - `SPIRIT` type (vodka, rum, gin, etc.)
  - `LIQUEUR` type (triple sec, coffee liqueur, etc.)
  - `JUICE` type (lime juice, orange juice, etc.)
  - `SYRUP` type (simple syrup, grenadine, etc.)
  - `SODA` type (club soda, tonic water, etc.)
  - `BITTERS` type (when measured in ml/dashes can be count-based too)
  - `WINE` and `BEER` types

## Usage Examples

### Backend (Kotlin)

When creating cocktails in code:

```kotlin
val mojito = Cocktail(
    name = "Mojito",
    ingredients = mutableListOf(
        CocktailIngredient(rumId, 60.0),        // 60 ml rum (volume)
        CocktailIngredient(limeJuiceId, 30.0),  // 30 ml lime juice (volume)
        CocktailIngredient(syrupId, 15.0),      // 15 ml simple syrup (volume)
        CocktailIngredient(mintLeavesId, -8.0), // 8 mint leaves (count)
        CocktailIngredient(sodaId, 60.0)        // 60 ml club soda (volume)
    ),
    steps = mutableListOf(
        "Muddle 8 mint leaves with syrup and lime juice",
        "Add rum and ice",
        "Top with club soda"
    ),
    tags = mutableListOf("refreshing", "minty")
)

val espressoMartini = Cocktail(
    name = "Espresso Martini",
    ingredients = mutableListOf(
        CocktailIngredient(vodkaId, 60.0),          // 60 ml vodka (volume)
        CocktailIngredient(coffeeLiqueurId, 15.0),  // 15 ml coffee liqueur (volume)
        CocktailIngredient(espressoId, 30.0),       // 30 ml espresso (volume)
        CocktailIngredient(coffeeBeansId, -3.0)     // 3 coffee beans (count)
    ),
    steps = mutableListOf(
        "Add all ingredients to shaker with ice",
        "Shake vigorously",
        "Strain into chilled martini glass",
        "Garnish with 3 coffee beans"
    ),
    tags = mutableListOf("coffee", "dessert")
)
```

### Frontend (TypeScript)

The UI automatically switches input mode based on ingredient type:

```typescript
// When user selects a GARNISH ingredient:
// - Input shows: "Count" field with "items" label
// - User enters: 8
// - System stores: -8.0 (negative value for count)

// When user selects a SPIRIT ingredient:
// - Input shows: "Amount" field with unit dropdown (ml/oz/cl)
// - User enters: 60 ml
// - System stores: 60.0 (positive value for volume)
```

### Display Formatting

The `MeasureService.formatMeasure()` method automatically formats measures:

```typescript
formatMeasure(60.0, MeasureUnit.ML)   // Returns: "60 ml"
formatMeasure(60.0, MeasureUnit.OZ)   // Returns: "2 oz"
formatMeasure(-8.0, MeasureUnit.ML)   // Returns: "8" (no unit for counts)
formatMeasure(-3.0, MeasureUnit.OZ)   // Returns: "3" (unit is ignored for counts)
```

## ABV and Base Spirit Calculations

The backend automatically filters out count-based ingredients (negative values) when calculating:

1. **ABV (Alcohol By Volume)**:
   - Only volume-based ingredients (positive values) are included in the calculation
   - Non-volume ingredients don't affect the ABV percentage

2. **Base Spirit Determination**:
   - Only volume-based spirits (positive values) are considered
   - The spirit with the highest volume is selected as the base spirit

Example:
```kotlin
// Mojito with 8 mint leaves
ingredients = listOf(
    CocktailIngredient(rumId, 60.0),        // Included in ABV calc
    CocktailIngredient(limeJuiceId, 30.0),  // Included in ABV calc  
    CocktailIngredient(syrupId, 15.0),      // Included in ABV calc
    CocktailIngredient(mintLeavesId, -8.0), // EXCLUDED from ABV calc
    CocktailIngredient(sodaId, 60.0)        // Included in ABV calc
)

// ABV calculated from: 60 + 30 + 15 + 60 = 165 ml total volume (mint leaves excluded)
// Base spirit: Rum (highest volume spirit at 60 ml)
```

## UI Behavior

### Adding Ingredients

1. **Select an ingredient** from the dropdown
2. **Automatic detection**: UI checks the ingredient type
3. **Input mode switches**:
   - For GARNISH/OTHER: Shows "Count" input with "items" label
   - For other types: Shows "Amount" input with unit selector (ml/oz/cl)
4. **Enter the value**:
   - Count mode: Enter whole numbers (1, 3, 8, etc.)
   - Volume mode: Enter decimal numbers (30, 45.5, 60, etc.)
5. **Click "Add"**: Ingredient is added to the cocktail

### Editing Ingredients

When editing a cocktail with count-based ingredients:
- The ingredient list displays counts without units (e.g., "8 Mint Leaves")
- Volume-based ingredients show with units (e.g., "60 ml White Rum")

## Common Use Cases

### Garnishes
- Mint leaves: 8-12 leaves for mojitos, mint juleps
- Coffee beans: 3 beans for espresso martini
- Lemon/lime wedges: 1 wedge for garnish
- Olives: 1-3 olives for martini
- Orange slices: 1 slice for old fashioned
- Maraschino cherries: 1-2 cherries for various cocktails

### Dashes and Drops
- Bitters: 2-3 dashes (can be stored as count)
- Absinthe: 1 dash/rinse (can be stored as count)
- Tabasco: 2-3 dashes

### Other Count-Based Items
- Egg whites: 1 egg white (for foam)
- Ice cream scoops: 1-2 scoops
- Sugar cubes: 1-2 cubes

## Best Practices

1. **Use counts for discrete items**: If you can count individual pieces, use count-based measure
2. **Use volume for liquids**: All pourable liquids should use volume measures
3. **Be consistent**: If a recipe says "8 mint leaves", enter 8 as a count, not a volume
4. **Document in steps**: Include count details in the recipe steps (e.g., "Muddle 8 mint leaves")
5. **Consider scale**: For professional/bar contexts, counts work well for small quantities (1-10 items)

## Developer Notes

### Testing

Tests are included to verify correct behavior:

```kotlin
@Test
fun `calculateAbv should ignore non-volume ingredients with negative measures`() {
    val ingredients = listOf(
        CocktailIngredient(rumId, 60.0),        // Volume
        CocktailIngredient(limeJuiceId, 30.0),  // Volume
        CocktailIngredient(mintLeavesId, -8.0)  // Count (ignored)
    )
    
    val result = patchCocktailService.calculateAbv(ingredients)
    
    // Should calculate ABV based only on 60ml + 30ml = 90ml
    assertEquals(26, result)
}
```

### Future Enhancements

Potential improvements to consider:

1. **Fractional counts**: Support 0.5 for "half a lime" or "half an egg"
2. **Unit types**: Add explicit "count", "dash", "pinch" units instead of just negative values
3. **Range support**: "8-10 mint leaves" for flexible recipes
4. **Weight measures**: Support grams/ounces for ingredients like sugar, salt
5. **Alternative display**: Show "8 leaves" vs "8 items" based on ingredient subtype

## Migration Notes

### Existing Data

- Existing cocktails with volume measures (positive values) work unchanged
- Cocktails currently using `-1.0` for garnishes will continue to work
- New cocktails can use any negative value for counts

### Database Schema

No database migration is required. The existing `measureMl` field (type `DOUBLE`) supports both positive and negative values.

## Support

For questions or issues related to non-volume measures:
- Check the issue tracker: [GitHub Issues](https://github.com/MrOdin2/CocktailDB/issues)
- Reference the original enhancement request: [ENHANCEMENT] Handling of not-volume measures
