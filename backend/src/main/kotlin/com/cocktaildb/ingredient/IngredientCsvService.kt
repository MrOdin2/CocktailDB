package com.cocktaildb.ingredient

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.StringReader

/**
 * Service for importing and exporting ingredients in CSV format
 */
@Service
class IngredientCsvService(
    private val ingredientDataService: IngredientDataService
) {

    /**
     * Export ingredients to CSV format
     * CSV format: name,type,abv,inStock,substituteNames,alternativeNames
     * - substituteNames: semicolon-separated substitute ingredient names
     * - alternativeNames: semicolon-separated alternative ingredient names
     */
    fun exportToCsv(): String {
        val ingredients = ingredientDataService.getAllIngredients()
        
        val sb = StringBuilder()
        // Header
        sb.append("name,type,abv,inStock,substituteNames,alternativeNames\n")
        
        for (ingredient in ingredients) {
            sb.append(escapeCsv(ingredient.name)).append(",")
            sb.append(ingredient.type.name).append(",")
            sb.append(ingredient.abv).append(",")
            sb.append(ingredient.inStock).append(",")
            
            // Substitutes (names)
            val substituteNames = ingredient.substitutes.map { it.name }
            sb.append(escapeCsv(substituteNames.joinToString(";"))).append(",")
            
            // Alternatives (names)
            val alternativeNames = ingredient.alternatives.map { it.name }
            sb.append(escapeCsv(alternativeNames.joinToString(";")))
            
            sb.append("\n")
        }
        
        return sb.toString()
    }

    /**
     * Import ingredients from CSV file with validation
     * Returns ImportResult with imported ingredients and errors
     */
    fun importFromCsv(file: MultipartFile): IngredientImportResult {
        val content = file.inputStream.bufferedReader().use { it.readText() }
        return importFromCsvContent(content)
    }

    /**
     * Import ingredients from CSV content string
     * This is a two-pass process:
     * 1. First pass: create all ingredients without relationships
     * 2. Second pass: add substitute and alternative relationships
     */
    fun importFromCsvContent(content: String): IngredientImportResult {
        val errors = mutableListOf<IngredientImportError>()
        val imported = mutableListOf<Ingredient>()
        
        val reader = BufferedReader(StringReader(content))
        val lines = reader.readLines()
        
        if (lines.isEmpty()) {
            errors.add(IngredientImportError(0, "Empty file", ""))
            return IngredientImportResult(imported, errors)
        }
        
        // First pass: create ingredients without relationships
        val newIngredients = mutableMapOf<String, Ingredient>()
        val relationshipsToAdd = mutableListOf<Triple<String, List<String>, List<String>>>() // (name, substitutes, alternatives)
        
        for ((index, line) in lines.drop(1).withIndex()) {
            val rowNumber = index + 2
            
            if (line.trim().isEmpty()) continue
            
            try {
                val parts = parseCsvLine(line)
                
                if (parts.size < 6) {
                    errors.add(IngredientImportError(rowNumber, "Invalid format: expected 6 columns", line))
                    continue
                }
                
                val name = parts[0].trim()
                if (name.isEmpty()) {
                    errors.add(IngredientImportError(rowNumber, "Ingredient name is required", line))
                    continue
                }
                
                // Check for duplicate
                val existingIngredient = ingredientDataService.getAllIngredients().find { 
                    it.name.equals(name, ignoreCase = true) 
                }
                if (existingIngredient != null) {
                    errors.add(IngredientImportError(rowNumber, "Ingredient with name '$name' already exists", line))
                    continue
                }
                
                // Parse type
                val typeStr = parts[1].trim().uppercase()
                val type = try {
                    IngredientType.valueOf(typeStr)
                } catch (e: IllegalArgumentException) {
                    errors.add(IngredientImportError(rowNumber, "Invalid ingredient type: $typeStr. Must be one of: ${IngredientType.values().joinToString(", ")}", line))
                    continue
                }
                
                // Parse ABV
                val abv = parts[2].trim().toIntOrNull()
                if (abv == null || abv < 0 || abv > 100) {
                    errors.add(IngredientImportError(rowNumber, "Invalid ABV: ${parts[2]}. Must be between 0 and 100", line))
                    continue
                }
                
                // Parse inStock
                val inStock = parts[3].trim().toBooleanStrictOrNull()
                if (inStock == null) {
                    errors.add(IngredientImportError(rowNumber, "Invalid inStock value: ${parts[3]}. Must be true or false", line))
                    continue
                }
                
                // Parse substitute and alternative names (will be processed in second pass)
                val substituteNames = parts[4].split(";").map { it.trim() }.filter { it.isNotEmpty() }
                val alternativeNames = parts[5].split(";").map { it.trim() }.filter { it.isNotEmpty() }
                
                // Create ingredient without relationships
                val ingredientDTO = IngredientDTO(
                    id = null,
                    name = name,
                    type = type,
                    abv = abv,
                    inStock = inStock,
                    substituteIds = emptySet(),
                    alternativeIds = emptySet()
                )
                
                val created = ingredientDataService.createIngredient(ingredientDTO)
                newIngredients[name] = created
                imported.add(created)
                
                // Store relationships for second pass
                if (substituteNames.isNotEmpty() || alternativeNames.isNotEmpty()) {
                    relationshipsToAdd.add(Triple(name, substituteNames, alternativeNames))
                }
                
            } catch (e: Exception) {
                errors.add(IngredientImportError(rowNumber, "Error parsing row: ${e.message}", line))
            }
        }
        
        // Second pass: add relationships
        val allIngredients = ingredientDataService.getAllIngredients()
        for ((ingredientName, substituteNames, alternativeNames) in relationshipsToAdd) {
            val ingredient = allIngredients.find { it.name.equals(ingredientName, ignoreCase = true) }
            if (ingredient == null) continue
            
            val substituteIds = mutableSetOf<Long>()
            val alternativeIds = mutableSetOf<Long>()
            
            // Find substitutes
            for (substituteName in substituteNames) {
                val substitute = allIngredients.find { it.name.equals(substituteName, ignoreCase = true) }
                if (substitute != null) {
                    substituteIds.add(substitute.id!!)
                }
            }
            
            // Find alternatives
            for (alternativeName in alternativeNames) {
                val alternative = allIngredients.find { it.name.equals(alternativeName, ignoreCase = true) }
                if (alternative != null) {
                    alternativeIds.add(alternative.id!!)
                }
            }
            
            // Update ingredient with relationships
            if (substituteIds.isNotEmpty() || alternativeIds.isNotEmpty()) {
                val dto = IngredientDTO(
                    id = ingredient.id,
                    name = ingredient.name,
                    type = ingredient.type,
                    abv = ingredient.abv,
                    inStock = ingredient.inStock,
                    substituteIds = substituteIds,
                    alternativeIds = alternativeIds
                )
                ingredientDataService.updateIngredient(dto)
            }
        }
        
        return IngredientImportResult(imported, errors)
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
data class IngredientImportResult(
    val imported: List<Ingredient>,
    val errors: List<IngredientImportError>
)

/**
 * Error during CSV import
 */
data class IngredientImportError(
    val row: Int,
    val message: String,
    val data: String
)
