# API Service Refactoring

## Overview
The monolithic `ApiService` has been split into smaller, domain-specific services following the Single Responsibility Principle and improving code maintainability.

## New Services

### 1. IngredientService (`ingredient.service.ts`)
Handles all ingredient-related API operations:
- `getAll()` - Get all ingredients
- `getById(id)` - Get ingredient by ID
- `create(ingredient)` - Create new ingredient
- `update(id, ingredient)` - Update ingredient
- `delete(id)` - Delete ingredient
- `getInStock()` - Get in-stock ingredients

**Location:** `frontend/src/app/services/ingredient.service.ts`

### 2. CocktailService (`cocktail.service.ts`)
Handles all cocktail-related API operations:
- `getAll()` - Get all cocktails
- `getById(id)` - Get cocktail by ID
- `create(cocktail)` - Create new cocktail
- `update(id, cocktail)` - Update cocktail
- `delete(id)` - Delete cocktail
- `getAvailable()` - Get available cocktails (based on in-stock ingredients)
- `search(name?, spirit?, tags?)` - Search cocktails

**Location:** `frontend/src/app/services/cocktail.service.ts`

### 3. SettingsService (`settings.service.ts`)
Handles application settings and theme operations:
- `getTheme()` - Get current theme
- `setTheme(theme)` - Set theme

**Location:** `frontend/src/app/services/settings.service.ts`

## Migration Summary

### Updated Components

#### Admin Components
- ✅ `IngredientsComponent` - Now uses `IngredientService`
- ✅ `CocktailsComponent` - Now uses `CocktailService` and `IngredientService`

#### Barkeeper Components
- ✅ `BarkeeperStockManagementComponent` - Now uses `IngredientService`

#### Visitor Components
- ✅ `VisitorCategoriesComponent` - Now uses `CocktailService`
- ✅ `VisitorCocktailListComponent` - Now uses `CocktailService`
- ✅ `VisitorRandomPickerComponent` - Now uses `CocktailService`
- ✅ `VisitorRecipeComponent` - Now uses `CocktailService` and `IngredientService`

#### Visualization Components
- ✅ `CocktailStatisticsComponent` - Now uses `CocktailService` and `IngredientService`
- ✅ `IngredientStatisticsComponent` - Now uses `CocktailService` and `IngredientService`

#### Services
- ✅ `ThemeService` - Now uses `SettingsService`

## Benefits

### 1. **Single Responsibility**
Each service now has a single, well-defined responsibility:
- IngredientService → Ingredient operations
- CocktailService → Cocktail operations
- SettingsService → Application settings

### 2. **Better Code Organization**
- Easier to locate specific API endpoints
- Clearer separation of concerns
- More intuitive naming (e.g., `ingredientService.getAll()` instead of `apiService.getAllIngredients()`)

### 3. **Improved Maintainability**
- Smaller, more focused services are easier to test
- Changes to one domain don't affect others
- Better encapsulation

### 4. **Scalability**
- Easy to add new domain-specific services as the app grows
- Can implement domain-specific caching or error handling
- Simpler to mock for testing

### 5. **Type Safety**
- Each service returns strongly-typed Observables
- Clear contracts for each domain

## Backward Compatibility

The original `ApiService` has been marked as `@deprecated` but remains in the codebase for now. It includes a deprecation notice pointing to the new services:

```typescript
/**
 * @deprecated This service has been split into domain-specific services.
 * Use the following services instead:
 * - IngredientService for ingredient operations
 * - CocktailService for cocktail operations
 * - SettingsService for settings/theme operations
 */
```

The service can be removed in a future version once all dependencies are confirmed to be updated.

## Usage Examples

### Before (Old ApiService)
```typescript
constructor(private apiService: ApiService) {}

loadIngredients() {
  this.apiService.getAllIngredients().subscribe(data => {
    this.ingredients = data;
  });
}
```

### After (New Services)
```typescript
constructor(private ingredientService: IngredientService) {}

loadIngredients() {
  this.ingredientService.getAll().subscribe(data => {
    this.ingredients = data;
  });
}
```

## Testing

All updated components should be tested to ensure:
1. ✅ No compilation errors
2. ✅ Services are properly injected
3. ✅ API calls work correctly
4. ✅ Error handling is maintained
5. ✅ Authentication (withCredentials) is preserved where needed

## Next Steps

1. Run the application and verify all functionality works correctly
2. Run tests if available
3. Monitor for any runtime errors
4. Consider removing the deprecated `ApiService` in a future release
5. Update any documentation that references the old `ApiService`

## Date
November 30, 2025

