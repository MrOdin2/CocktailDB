package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.IngredientRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.StringReader

/**
 * Service for importing and exporting cocktails in CSV format
 */
@Service
class CocktailCsvService(
    private val cocktailDataService: CocktailDataService,
    private val patchCocktailService: PatchCocktailService,
    private val ingredientRepository: IngredientRepository
) {

    /**
     * Export cocktails to CSV format
     * CSV format: name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId
     * - ingredients: semicolon-separated ingredient names
     * - ingredientAmounts: semicolon-separated amounts in ml
     * - steps: pipe-separated preparation steps
     * - tags: semicolon-separated tags
     * - glasswareTypes: semicolon-separated glassware types
     * - iceTypes: semicolon-separated ice types
     */
    fun exportToCsv(): String {
        val cocktails = cocktailDataService.getAllCocktails()
        val allIngredients = ingredientRepository.findAll()
        
        val sb = StringBuilder()
        // Header
        sb.append("name,ingredients,ingredientAmounts,steps,notes,tags,glasswareTypes,iceTypes,variationOfId\n")
        
        for (cocktail in cocktails) {
            sb.append(escapeCsv(cocktail.name)).append(",")
            
            // Ingredients (names)
            val ingredientNames = cocktail.ingredients.map { ing ->
                val ingredient = allIngredients.find { it.id == ing.ingredientId }
                ingredient?.name ?: "Unknown"
            }
            sb.append(escapeCsv(ingredientNames.joinToString(";"))).append(",")
            
            // Ingredient amounts
            val amounts = cocktail.ingredients.map { it.measureMl.toString() }
            sb.append(escapeCsv(amounts.joinToString(";"))).append(",")
            
            // Steps (pipe-separated to avoid conflict with semicolons)
            sb.append(escapeCsv(cocktail.steps.joinToString("|"))).append(",")
            
            // Notes
            sb.append(escapeCsv(cocktail.notes ?: "")).append(",")
            
            // Tags
            sb.append(escapeCsv(cocktail.tags.joinToString(";"))).append(",")
            
            // Glassware types
            sb.append(escapeCsv(cocktail.glasswareTypes.joinToString(";"))).append(",")
            
            // Ice types
            sb.append(escapeCsv(cocktail.iceTypes.joinToString(";"))).append(",")
            
            // Variation of ID
            sb.append(cocktail.variationOfId?.toString() ?: "")
            
            sb.append("\n")
        }
        
        return sb.toString()
    }

    /**
     * Import cocktails from CSV file with validation
     * Returns ImportResult with imported cocktails and errors
     */
    fun importFromCsv(file: MultipartFile): ImportResult {
        val content = file.inputStream.bufferedReader().use { it.readText() }
        return importFromCsvContent(content)
    }

    /**
     * Import cocktails from CSV content string
     */
    fun importFromCsvContent(content: String): ImportResult {
        val errors = mutableListOf<ImportError>()
        val imported = mutableListOf<Cocktail>()
        val allIngredients = ingredientRepository.findAll()
        
        val reader = BufferedReader(StringReader(content))
        val lines = reader.readLines()
        
        if (lines.isEmpty()) {
            errors.add(ImportError(0, "Empty file", ""))
            return ImportResult(imported, errors)
        }
        
        // Skip header
        for ((index, line) in lines.drop(1).withIndex()) {
            val rowNumber = index + 2 // +2 because we skip header and are 1-indexed
            
            if (line.trim().isEmpty()) continue
            
            try {
                val parts = parseCsvLine(line)
                
                if (parts.size < 9) {
                    errors.add(ImportError(rowNumber, "Invalid format: expected 9 columns", line))
                    continue
                }
                
                val name = parts[0].trim()
                if (name.isEmpty()) {
                    errors.add(ImportError(rowNumber, "Cocktail name is required", line))
                    continue
                }
                
                // Parse ingredients
                val ingredientNames = parts[1].split(";").map { it.trim() }.filter { it.isNotEmpty() }
                val ingredientAmounts = parts[2].split(";").map { it.trim() }.filter { it.isNotEmpty() }
                
                if (ingredientNames.size != ingredientAmounts.size) {
                    errors.add(ImportError(rowNumber, "Ingredient names and amounts count mismatch", line))
                    continue
                }
                
                val cocktailIngredients = mutableListOf<CocktailIngredient>()
                var hasIngredientError = false
                
                for (i in ingredientNames.indices) {
                    val ingredientName = ingredientNames[i]
                    val ingredient = allIngredients.find { it.name.equals(ingredientName, ignoreCase = true) }
                    
                    if (ingredient == null) {
                        errors.add(ImportError(rowNumber, "Ingredient not found: $ingredientName", line))
                        hasIngredientError = true
                        break
                    }
                    
                    val amount = ingredientAmounts[i].toDoubleOrNull()
                    if (amount == null || amount < 0) {
                        errors.add(ImportError(rowNumber, "Invalid amount for ingredient $ingredientName: ${ingredientAmounts[i]}", line))
                        hasIngredientError = true
                        break
                    }
                    
                    cocktailIngredients.add(CocktailIngredient(ingredient.id!!, amount))
                }
                
                if (hasIngredientError) continue
                
                // Parse steps
                val steps = parts[3].split("|").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                
                // Parse notes
                val notes = parts[4].trim().ifEmpty { null }
                
                // Parse tags
                val tags = parts[5].split(";").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                
                // Parse glassware types
                val glasswareTypes = parts[6].split(";").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                
                // Parse ice types
                val iceTypes = parts[7].split(";").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                
                // Parse variation of ID
                val variationOfId = parts[8].trim().toLongOrNull()
                
                // Check for duplicate name
                val existing = cocktailDataService.getAllCocktails().find { it.name.equals(name, ignoreCase = true) }
                if (existing != null) {
                    errors.add(ImportError(rowNumber, "Cocktail with name '$name' already exists", line))
                    continue
                }
                
                // Create cocktail
                val cocktail = Cocktail(
                    name = name,
                    ingredients = cocktailIngredients,
                    steps = steps,
                    notes = notes,
                    tags = tags,
                    glasswareTypes = glasswareTypes,
                    iceTypes = iceTypes,
                    variationOfId = variationOfId
                )
                
                val created = patchCocktailService.createCocktail(cocktail)
                imported.add(created)
                
            } catch (e: Exception) {
                errors.add(ImportError(rowNumber, "Error parsing row: ${e.message}", line))
            }
        }
        
        return ImportResult(imported, errors)
    }

    /**
     * Parse a CSV line handling quoted fields
     */
    private fun parseCsvLine(line: String): List<String> {
        val parts = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        
        while (i < line.length) {
            val char = line[i]
            
            when {
                char == '"' && (i == 0 || line[i - 1] != '\\') -> {
                    inQuotes = !inQuotes
                }
                char == ',' && !inQuotes -> {
                    parts.add(current.toString().trim().removeSurrounding("\""))
                    current.clear()
                }
                else -> {
                    current.append(char)
                }
            }
            i++
        }
        
        // Add last part
        parts.add(current.toString().trim().removeSurrounding("\""))
        
        return parts
    }

    /**
     * Escape CSV field (add quotes if contains comma, quote, or newline)
     */
    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"${value.replace("\"", "\"\"")}\""
        }
        return value
    }
}

/**
 * Result of CSV import operation
 */
data class ImportResult(
    val imported: List<Cocktail>,
    val errors: List<ImportError>
)

/**
 * Error during CSV import
 */
data class ImportError(
    val row: Int,
    val message: String,
    val data: String
)
