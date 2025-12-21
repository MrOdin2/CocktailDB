# CSV Import/Export Guide

## Overview

CocktailDB supports importing and exporting data in CSV format, allowing you to:
- Back up your cocktail and ingredient data
- Share recipes with others
- Bulk import data from other sources
- Migrate data between instances

## CSV Format Specifications

### Ingredients CSV Format

**File:** `ingredients-template.csv`

**Columns:**
1. `name` - Ingredient name (required, unique)
2. `type` - Ingredient type (required, must be one of: SPIRIT, LIQUEUR, WINE, BEER, JUICE, SODA, SYRUP, BITTERS, GARNISH, OTHER)
3. `abv` - Alcohol by volume percentage (required, 0-100)
4. `inStock` - Stock status (required, true or false)
5. `substituteNames` - Semicolon-separated list of substitute ingredient names (optional)
6. `alternativeNames` - Semicolon-separated list of alternative ingredient names (optional)

**Example:**
```csv
name,type,abv,inStock,substituteNames,alternativeNames
Vodka,SPIRIT,40,true,Gin,
Gin,SPIRIT,40,true,Vodka,
Simple Syrup,SYRUP,0,true,,Sugar Syrup
Lime Juice,JUICE,0,true,Lemon Juice,
Mint,GARNISH,0,true,,Basil
```

**Notes:**
- Substitute relationships are bidirectional (if A is a substitute for B, B becomes a substitute for A)
- Ingredient names in relationships must exist in the database (create base ingredients first)
- Use empty values for optional fields (e.g., `,,` for no substitutes or alternatives)
- Special characters and commas in values will be automatically quoted

### Cocktails CSV Format

**File:** `cocktails-template.csv`

**Columns:**
1. `name` - Cocktail name (required, unique)
2. `ingredients` - Semicolon-separated list of ingredient names (required)
3. `ingredientAmounts` - Semicolon-separated list of amounts in ml (required, must match ingredient count)
4. `steps` - Pipe-separated list of preparation steps (required)
5. `notes` - Additional notes (optional)
6. `tags` - Semicolon-separated list of tags (optional)
7. `glasswareTypes` - Semicolon-separated list of glassware types (optional)
8. `iceTypes` - Semicolon-separated list of ice types (optional)
9. `variationOfId` - ID of base cocktail if this is a variation (optional)

**Example:**
```csv
name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
Mojito,Rum;Lime Juice;Mint;Simple Syrup;Soda Water,60;30;10;15;60,Muddle mint and lime in glass|Add rum and simple syrup|Fill with ice|Top with soda water|Garnish with mint sprig,Best served in summer,refreshing;summer;classic,Highball,Crushed,
Gin and Tonic,Gin;Tonic Water;Lime,60;120;5,Fill glass with ice|Add gin|Top with tonic water|Squeeze lime wedge,Use premium tonic for best results,refreshing;easy,Highball,Cubed,
```

**Notes:**
- Ingredient names must exist in the database before importing cocktails
- Amounts are always in milliliters (ml)
- Use pipe (|) to separate steps to avoid conflicts with semicolons in text
- ABV and base spirit are automatically calculated from ingredients
- variationOfId must reference an existing cocktail ID (leave empty if not a variation)

## Importing Data

### Prerequisites
1. You must be logged in as an admin user
2. For cocktails, all ingredients referenced in the CSV must already exist in the database
3. CSV files must be UTF-8 encoded

### Import Process

#### From Admin Panel

**Ingredients:**
1. Navigate to Admin → Ingredients
2. Click "Import CSV" button
3. Select your CSV file
4. Review import results:
   - Successfully imported items
   - Errors with row numbers and descriptions
5. Fix any errors and re-import if needed

**Cocktails:**
1. Navigate to Admin → Cocktails
2. Click "Import CSV" button
3. Select your CSV file
4. Review import results:
   - Successfully imported items
   - Errors with row numbers and descriptions
5. Fix any errors and re-import if needed

### Import Validation

The system validates:
- **Format**: Correct number of columns, valid CSV structure
- **Required fields**: All required columns have values
- **Data types**: Numeric values, boolean values, enum values
- **References**: Ingredient names exist, variation IDs exist
- **Duplicates**: Names are unique (imports skip duplicates)
- **Relationships**: Ingredient counts match amount counts

### Error Handling

If errors occur during import:
1. The system displays error messages with row numbers
2. Successfully imported items are still saved
3. Failed items are skipped
4. Fix errors in your CSV file and try again
5. Previously imported items won't be duplicated

## Exporting Data

### Export Process

#### Ingredients
1. Navigate to Admin → Ingredients
2. Click "Export CSV" button
3. File downloads automatically as `ingredients-YYYY-MM-DD.csv`
4. All ingredients with current stock status and relationships are exported

#### Cocktails
1. Navigate to Admin → Cocktails
2. Apply any filters (name, spirit, tags, availability) to select specific cocktails
3. Click "Export CSV" button
4. File downloads automatically as `cocktails-YYYY-MM-DD.csv`
5. Only filtered cocktails are exported

### Export Features

- **Complete data**: All fields including relationships are exported
- **UTF-8 encoding**: Special characters are preserved
- **Automatic quoting**: Fields with commas or quotes are properly quoted
- **Readable format**: Can be opened in Excel, Google Sheets, or any CSV editor
- **Backup friendly**: Files include date stamp for versioning

## Best Practices

### Creating CSV Files

1. **Use UTF-8 encoding** to support special characters
2. **Don't use commas** in field values (or they'll be quoted)
3. **Use semicolons** to separate list items within fields
4. **Use pipes (|)** to separate steps in cocktail recipes
5. **Test with small batches** before importing large datasets
6. **Keep backups** of your CSV files

### Data Organization

1. **Import ingredients first**, then cocktails
2. **Verify ingredient names** match exactly (case-insensitive but spelling matters)
3. **Start simple** - add basic data first, then add relationships
4. **Use consistent naming** for tags, glassware, and ice types
5. **Document your process** if importing from external sources

### Troubleshooting

**Common Issues:**

1. **"Ingredient not found"** - Ensure all ingredients exist before importing cocktails
2. **"Invalid ABV"** - Must be a number between 0 and 100
3. **"Invalid type"** - Check spelling of ingredient type (SPIRIT, LIQUEUR, etc.)
4. **"Name already exists"** - Either delete the existing item or use a different name
5. **"Invalid format: expected X columns"** - Verify your CSV has all required columns

**Tips:**

- Open CSV files in a text editor to check formatting
- Check for extra commas or missing fields
- Ensure no extra blank lines at the end
- Verify quotes are properly closed
- Test with the template files first

## Security Considerations

- CSV import requires admin authentication
- CSV export requires authentication (admin or barkeeper)
- Files are processed server-side with validation
- No SQL injection risk - all data is parameterized
- File size limits apply (check server configuration)

## Additional Resources

- See `ingredients-template.csv` for a complete ingredient example
- See `cocktails-template.csv` for a complete cocktail example
- Check the admin panel for real-time validation feedback
- Refer to the API documentation for programmatic access

## API Endpoints

For programmatic access:

**Export:**
- `GET /api/ingredients/export/csv` - Export all ingredients
- `GET /api/cocktails/export/csv` - Export all cocktails

**Import:**
- `POST /api/ingredients/import/csv` - Import ingredients (multipart/form-data)
- `POST /api/cocktails/import/csv` - Import cocktails (multipart/form-data)

All endpoints require authentication via session cookie.
