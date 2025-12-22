package com.cocktaildb.ingredient

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredients", description = "Ingredient management and inventory endpoints")
class IngredientController(
    private val ingredientDataService: IngredientDataService,
    private val patchIngredientService: PatchIngredientService,
    private val ingredientCsvService: IngredientCsvService,
) {

    @GetMapping
    @Operation(
        summary = "Get all ingredients",
        description = "Retrieve a list of all ingredients in the database"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of ingredients retrieved successfully",
        content = [Content(array = ArraySchema(schema = Schema(implementation = IngredientDTO::class)))]
    )
    fun getAllIngredients(): List<IngredientDTO> {
        return ingredientDataService.getAllIngredients().toDTOs()
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get ingredient by ID",
        description = "Retrieve a specific ingredient by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ingredient found",
                content = [Content(schema = Schema(implementation = IngredientDTO::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ingredient not found"
            )
        ]
    )
    fun getIngredientById(
        @Parameter(description = "ID of the ingredient to retrieve", required = true)
        @PathVariable id: Long
    ): ResponseEntity<IngredientDTO> {
        val ingredient = ingredientDataService.getIngredientById(id)
        return if (ingredient != null) {
            ResponseEntity.ok(ingredient.toDTO())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    @Operation(
        summary = "Create a new ingredient",
        description = "Create a new ingredient with type, ABV, and stock status. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Ingredient created successfully",
                content = [Content(schema = Schema(implementation = IngredientDTO::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun createIngredient(@RequestBody ingredientDTO: IngredientDTO): ResponseEntity<IngredientDTO> {
        val created = ingredientDataService.createIngredient(ingredientDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toDTO())
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing ingredient",
        description = "Update an ingredient's information. Stock changes trigger SSE notifications. Requires admin or barkeeper authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ingredient updated successfully",
                content = [Content(schema = Schema(implementation = IngredientDTO::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - ID mismatch"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ingredient not found"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun updateIngredient(
        @Parameter(description = "ID of the ingredient to update", required = true)
        @PathVariable id: Long,
        @RequestBody ingredientDTO: IngredientDTO
    ): ResponseEntity<IngredientDTO> {
        if (ingredientDTO.id == null || ingredientDTO.id != id) {
            return ResponseEntity.badRequest().build()
        }
        val updated = patchIngredientService.updateIngredient(ingredientDTO)
        return if (updated != null) {
            ResponseEntity.ok(updated.toDTO())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an ingredient",
        description = "Delete an ingredient from the database. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Ingredient deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ingredient not found"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun deleteIngredient(
        @Parameter(description = "ID of the ingredient to delete", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        return if (ingredientDataService.getIngredientById(id) != null) {
            ingredientDataService.deleteIngredient(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/in-stock")
    @Operation(
        summary = "Get in-stock ingredients",
        description = "Retrieve a list of all ingredients that are currently in stock"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of in-stock ingredients retrieved successfully",
        content = [Content(array = ArraySchema(schema = Schema(implementation = IngredientDTO::class)))]
    )
    fun getInStockIngredients(): List<IngredientDTO> {
        return ingredientDataService.getInStockIngredients().toDTOs()
    }

    @GetMapping("/export/csv")
    @Operation(
        summary = "Export ingredients to CSV",
        description = "Export all ingredients to CSV format for backup or sharing"
    )
    @ApiResponse(
        responseCode = "200",
        description = "CSV file generated successfully"
    )
    fun exportIngredientsCsv(): ResponseEntity<String> {
        val csv = ingredientCsvService.exportToCsv()
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("text/csv")
        headers.setContentDispositionFormData("attachment", "ingredients.csv")
        return ResponseEntity.ok().headers(headers).body(csv)
    }

    @PostMapping("/import/csv")
    @Operation(
        summary = "Import ingredients from CSV",
        description = "Import ingredients from CSV file. Validates data and reports errors. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Import completed with results",
                content = [Content(schema = Schema(implementation = IngredientImportResult::class))]
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
    fun importIngredientsCsv(
        @Parameter(description = "CSV file to import", required = true)
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Any> {
        if (file.isEmpty) {
            val error = mapOf(
                "imported" to emptyList<Ingredient>(),
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
        
        val result = ingredientCsvService.importFromCsv(file)
        return ResponseEntity.ok(result)
    }
}