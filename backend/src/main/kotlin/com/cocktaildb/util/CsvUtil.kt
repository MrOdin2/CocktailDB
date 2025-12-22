package com.cocktaildb.util

import java.io.BufferedReader
import java.io.StringReader

/**
 * Utility class for CSV parsing and generation
 */
object CsvUtil {
    
    /**
     * Parse a CSV line handling quoted fields according to RFC 4180
     * Properly handles escaped quotes (double quotes "")
     */
    fun parseCsvLine(line: String): List<String> {
        val parts = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        
        while (i < line.length) {
            val char = line[i]
            
            when {
                char == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        // Escaped quote (double quote)
                        current.append('"')
                        i++ // Skip next quote
                    } else {
                        // Toggle quote mode
                        inQuotes = !inQuotes
                    }
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
     * Escape CSV field according to RFC 4180
     * Adds quotes if field contains comma, quote, or newline
     * Escapes quotes by doubling them
     */
    fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"${value.replace("\"", "\"\"")}\""
        }
        return value
    }
}
