# Ingredient Substitution and Alternatives Feature

## Overview

The Ingredient Substitution and Alternatives feature allows users to define relationships between ingredients, enabling more flexible cocktail preparation and better inventory management.

## Feature Description

### Substitutes

Substitutes are ingredients that can be used as direct replacements for each other. These are typically:
- Generic vs. branded ingredients (e.g., "Coconut Rum" instead of "Malibu")
- Very similar items (e.g., "Orange Wheel" vs. "Orange Slice")
- Interchangeable variants of the same ingredient

**Use Case**: When making a Piña Colada that calls for "Malibu," you can use generic "Coconut Rum" as a substitute without significantly changing the cocktail.

### Alternatives

Alternatives are noticeably different ingredients that can still be used, though they may alter the cocktail's character:
- Similar spirits from different categories (e.g., "Champagne" vs. "Prosecco")
- Compound substitutions (e.g., "Vodka + Vanilla Syrup" instead of "Vanilla Vodka")
- Different but compatible ingredients

**Use Case**: When making a Mimosa that calls for "Prosecco," you can use "Champagne" as an alternative, knowing it will create a similar but slightly different result.

## Database Schema

### Tables

**`ingredient_substitutes`** (Many-to-Many)
- `ingredient_id` (BIGINT, FK to ingredients.id)
- `substitute_id` (BIGINT, FK to ingredients.id)
- Primary Key: (ingredient_id, substitute_id)
- Constraint: ingredient_id != substitute_id

**`ingredient_alternatives`** (Many-to-Many)
- `ingredient_id` (BIGINT, FK to ingredients.id)
- `alternative_id` (BIGINT, FK to ingredients.id)
- Primary Key: (ingredient_id, alternative_id)
- Constraint: ingredient_id != alternative_id

### Relationships

Both tables implement bidirectional many-to-many relationships. When you mark ingredient A as a substitute for ingredient B, the relationship works in both directions.

## API Endpoints

### Get All Ingredients (Updated)
```
GET /api/ingredients
```

**Response**:
```json
[
  {
    "id": 1,
    "name": "Malibu",
    "type": "LIQUEUR",
    "abv": 21,
    "inStock": true,
    "substituteIds": [2],
    "alternativeIds": []
  },
  {
    "id": 2,
    "name": "Coconut Rum",
    "type": "SPIRIT",
    "abv": 40,
    "inStock": false,
    "substituteIds": [1],
    "alternativeIds": []
  }
]
```

### Create/Update Ingredient (Updated)
```
POST /api/ingredients
PUT /api/ingredients/{id}
```

**Request Body**:
```json
{
  "name": "Champagne",
  "type": "WINE",
  "abv": 12,
  "inStock": true,
  "substituteIds": [],
  "alternativeIds": [5]
}
```

### Get Available Cocktails with Substitutions
```
GET /api/cocktails/available-with-substitutions
```

**Response**:
```json
{
  "exact": [
    // Cocktails that can be made with exact ingredients in stock
  ],
  "withSubstitutes": [
    // Cocktails that can be made using substitutes
  ],
  "withAlternatives": [
    // Cocktails that can be made using alternatives
  ]
}
```

## Frontend Implementation

### Ingredient Management UI

1. **Add/Edit Ingredient Modal**: Includes checkbox lists for selecting substitutes and alternatives
2. **Table View**: Displays relationship tags for each ingredient
3. **Card View (Mobile)**: Shows relationships in a card format
4. **Inline Editing**: Expanded row for editing relationships in table view

### User Interface Elements

- **Relationship Tags**: Color-coded badges showing related ingredients
- **Checkbox Lists**: Scrollable lists for managing relationships
- **Help Text**: Tooltips explaining the difference between substitutes and alternatives

### Translation Support

The feature is fully internationalized with translations in:
- English (en.ts)
- German (de.ts)

Translation keys:
- `ingredients.substitutes`: "Substitutes" / "Ersatzstoffe"
- `ingredients.alternatives`: "Alternatives" / "Alternativen"
- `ingredients.substitutesHelp`: Help text for substitutes
- `ingredients.alternativesHelp`: Help text for alternatives

## Backend Implementation

### Data Transfer Objects (DTOs)

**IngredientDTO** handles serialization of ingredient relationships without circular references:
```kotlin
data class IngredientDTO(
    val id: Long?,
    val name: String,
    val type: IngredientType,
    val abv: Int,
    val inStock: Boolean,
    val substituteIds: Set<Long> = emptySet(),
    val alternativeIds: Set<Long> = emptySet()
)
```

### Service Layer

**CocktailSearchService.getAvailableCocktailsWithSubstitutions()**:
1. Gets all in-stock ingredients
2. Builds sets of available ingredients including substitutes and alternatives
3. Categorizes cocktails based on ingredient availability:
   - Exact: All ingredients are in stock
   - With Substitutes: Can be made using substitutes
   - With Alternatives: Can be made using alternatives

### Migration

Database migration **V3__Add_ingredient_substitutes_and_alternatives.sql** creates the necessary tables and indexes.

## Testing

The feature includes comprehensive test coverage:

### Backend Tests
- **CocktailSearchServiceSubstitutionTest** (7 tests):
  - Exact ingredient matching
  - Substitute matching
  - Alternative matching
  - Multiple categories
  - Empty inventory handling
  - Priority handling (exact > substitutes > alternatives)

### Test Patterns
```kotlin
@Test
fun `getAvailableCocktailsWithSubstitutions should categorize cocktails with exact ingredients`() {
    // Given: Setup ingredients with relationships
    // When: Call getAvailableCocktailsWithSubstitutions()
    // Then: Verify correct categorization
}
```

## Usage Examples

### Setting Up Substitutes

1. Navigate to Ingredients page
2. Click "Edit" on an ingredient (e.g., "Malibu")
3. In the "Substitutes" section, check "Coconut Rum"
4. Save changes

Result: Both "Malibu" and "Coconut Rum" now appear as substitutes for each other.

### Setting Up Alternatives

1. Navigate to Ingredients page
2. Click "Edit" on an ingredient (e.g., "Champagne")
3. In the "Alternatives" section, check "Prosecco"
4. Save changes

Result: Cocktails requiring Prosecco will show as available with alternatives when you have Champagne in stock.

### Viewing Available Cocktails

The `/api/cocktails/available-with-substitutions` endpoint returns:
- **exact**: Cocktails you can make exactly as specified
- **withSubstitutes**: Cocktails you can make with minor substitutions
- **withAlternatives**: Cocktails you can make with notable differences

## Future Enhancements

1. **Recipe Display**: Show alternative ingredients in recipe view
   - Subtle indication when ingredient is in stock
   - Prominent suggestion when original ingredient is out of stock

2. **Smart Recommendations**: 
   - Calculate which ingredient purchase would unlock the most new recipes
   - Split into "new available" and "new available with alternatives"

3. **Substitution Notes**: Allow custom notes explaining the difference
   - "This will make the drink slightly sweeter"
   - "Different flavor profile but similar strength"

4. **User Preferences**: Remember which substitutions users prefer

## Technical Notes

### Bidirectional Relationships

Relationships are bidirectional by design. When you mark A as a substitute for B, B automatically becomes a substitute for A. This is implemented at the service layer, not the database level.

### Performance Considerations

- Relationships are loaded lazily (FetchType.LAZY)
- DTOs prevent N+1 query problems
- Indexes on foreign keys ensure efficient lookups

### Security

- Ingredient modification requires authentication
- Public endpoints return read-only DTOs
- Circular relationships are prevented by database constraints

## Migration Guide

For existing deployments:

1. **Backup Database**: Always backup before migration
2. **Run Migration**: V3 migration runs automatically on startup
3. **No Data Loss**: Existing ingredients remain unchanged
4. **Backward Compatible**: New fields default to empty arrays
5. **Gradual Adoption**: Relationships are optional

## Support

For issues or questions:
- Check the test files for usage examples
- Review the API documentation above
- Consult the code comments in service classes
