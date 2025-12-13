package com.cocktaildb.controller

import com.cocktaildb.appsettings.AppSettingsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Schema(description = "Theme response")
data class ThemeResponse(
    @Schema(description = "Current theme (light or dark)", required = true, example = "dark")
    val theme: String
)

@Schema(description = "Theme update request")
data class ThemeRequest(
    @Schema(description = "New theme to set (light or dark)", required = true, example = "dark")
    val theme: String
)

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings", description = "Application settings management endpoints")
class SettingsController(
    private val appSettingsService: AppSettingsService
) {
    
    @GetMapping("/theme")
    @Operation(
        summary = "Get current theme",
        description = "Retrieve the current application theme setting"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Theme retrieved successfully",
        content = [Content(schema = Schema(implementation = ThemeResponse::class))]
    )
    fun getTheme(): ResponseEntity<ThemeResponse> {
        val theme = appSettingsService.getTheme()
        return ResponseEntity.ok(ThemeResponse(theme))
    }
    
    @PutMapping("/theme")
    @Operation(
        summary = "Set application theme",
        description = "Update the application theme setting. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Theme updated successfully",
                content = [Content(schema = Schema(implementation = ThemeResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid theme value"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - authentication required"
            )
        ]
    )
    fun setTheme(@RequestBody request: ThemeRequest): ResponseEntity<ThemeResponse> {
        // TODO: Add admin authentication check when auth is implemented
        return try {
            val theme = appSettingsService.setTheme(request.theme)
            ResponseEntity.ok(ThemeResponse(theme))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}
