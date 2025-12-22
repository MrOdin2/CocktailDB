package com.cocktaildb.cocktail

import com.cocktaildb.ingredient.CocktailsWithSubstitutionsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/cocktails")
@Tag(name = "Cocktails", description = "Cocktail management and search endpoints")
class CocktailController(
    private val cocktailDataService: CocktailDataService,
    private val cocktailSearchService: CocktailSearchService,
    private val patchCocktailService: PatchCocktailService,
    private val cocktailCsvService: CocktailCsvService,
) {

    @GetMapping
    @Operation(
        summary = "Get all cocktails",
        description = "Retrieve a list of all cocktails in the database"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of cocktails retrieved successfully",
        content = [Content(array = ArraySchema(schema = Schema(implementation = Cocktail::class)))]
    )
    fun getAllCocktails(): List<Cocktail> {
        return cocktailDataService.getAllCocktails()
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get cocktail by ID",
        description = "Retrieve a specific cocktail by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Cocktail found",
                content = [Content(schema = Schema(implementation = Cocktail::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Cocktail not found"
            )
        ]
    )
    fun getCocktailById(
        @Parameter(description = "ID of the cocktail to retrieve", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Cocktail> {
        val cocktail = cocktailDataService.getCocktailById(id)
        return if (cocktail != null) {
            ResponseEntity.ok(cocktail)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    @Operation(
        summary = "Create a new cocktail",
        description = "Create a new cocktail with ingredients and preparation steps. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Cocktail created successfully",
                content = [Content(schema = Schema(implementation = Cocktail::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun createCocktail(@RequestBody cocktail: Cocktail): ResponseEntity<Cocktail> {
        val created = patchCocktailService.createCocktail(cocktail)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing cocktail",
        description = "Update a cocktail's information. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Cocktail updated successfully",
                content = [Content(schema = Schema(implementation = Cocktail::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Cocktail not found"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun updateCocktail(
        @Parameter(description = "ID of the cocktail to update", required = true)
        @PathVariable id: Long,
        @RequestBody cocktail: Cocktail
    ): ResponseEntity<Cocktail> {
        val updated = patchCocktailService.updateCocktail(id, cocktail)
        return if (updated != null) {
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a cocktail",
        description = "Delete a cocktail from the database. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Cocktail deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Cocktail not found"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun deleteCocktail(
        @Parameter(description = "ID of the cocktail to delete", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val success = cocktailDataService.deleteCocktail(id)
        return if (success){
            ResponseEntity.ok().build()
        }else{
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/available")
    @Operation(
        summary = "Get available cocktails",
        description = "Get cocktails that can be made with currently in-stock ingredients"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of available cocktails retrieved successfully",
        content = [Content(array = ArraySchema(schema = Schema(implementation = Cocktail::class)))]
    )
    fun getAvailableCocktails(): List<Cocktail> {
        return cocktailSearchService.getAvailableCocktails()
    }

    @GetMapping("/available-with-substitutions")
    @Operation(
        summary = "Get available cocktails with substitutions",
        description = "Get cocktails categorized by availability: exact matches, with substitutes, and with alternatives"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Cocktails with substitution information retrieved successfully",
        content = [Content(schema = Schema(implementation = CocktailsWithSubstitutionsResponse::class))]
    )
    fun getAvailableCocktailsWithSubstitutions(): CocktailsWithSubstitutionsResponse {
        return cocktailSearchService.getAvailableCocktailsWithSubstitutions()
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search cocktails",
        description = "Search for cocktails by name, base spirit, or tags"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Search results retrieved successfully",
        content = [Content(array = ArraySchema(schema = Schema(implementation = Cocktail::class)))]
    )
    fun searchCocktails(
        @Parameter(description = "Filter by cocktail name (case-insensitive partial match)")
        @RequestParam(required = false) name: String?,
        @Parameter(description = "Filter by base spirit")
        @RequestParam(required = false) spirit: String?,
        @Parameter(description = "Filter by tags (all tags must match)")
        @RequestParam(required = false) tags: List<String>?
    ): List<Cocktail> {
        return cocktailSearchService.searchCocktails(name, spirit, tags)
    }

    @GetMapping("/export/csv")
    @Operation(
        summary = "Export cocktails to CSV",
        description = "Export all cocktails to CSV format for backup or sharing"
    )
    @ApiResponse(
        responseCode = "200",
        description = "CSV file generated successfully"
    )
    fun exportCocktailsCsv(): ResponseEntity<String> {
        val csv = cocktailCsvService.exportToCsv()
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("text/csv")
        headers.setContentDispositionFormData("attachment", "cocktails.csv")
        return ResponseEntity.ok().headers(headers).body(csv)
    }

    @PostMapping("/import/csv")
    @Operation(
        summary = "Import cocktails from CSV",
        description = "Import cocktails from CSV file. Validates data and reports errors. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Import completed with results",
                content = [Content(schema = Schema(implementation = ImportResult::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid file format"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun importCocktailsCsv(
        @Parameter(description = "CSV file to import", required = true)
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Any> {
        if (file.isEmpty) {
            val error = mapOf(
                "imported" to emptyList<Cocktail>(),
                "errors" to listOf(
                    mapOf(
                        "row" to 0,
                        "message" to "No file uploaded or file is empty",
                        "data" to ""
                    )
                )
            )
            return ResponseEntity.badRequest().body(error)
        }
        
        val result = cocktailCsvService.importFromCsv(file)
        return ResponseEntity.ok(result)
    }
}