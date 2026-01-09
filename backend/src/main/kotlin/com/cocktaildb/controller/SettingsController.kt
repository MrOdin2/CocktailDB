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

@Schema(description = "Customer authentication status response")
data class CustomerAuthStatusResponse(
    @Schema(description = "Whether customer authentication is enabled", required = true)
    val enabled: Boolean
)

@Schema(description = "Customer authentication update request")
data class CustomerAuthUpdateRequest(
    @Schema(description = "Enable or disable customer authentication", required = true)
    val enabled: Boolean
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
        return try {
            val theme = appSettingsService.setTheme(request.theme)
            ResponseEntity.ok(ThemeResponse(theme))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @GetMapping("/customer-auth")
    @Operation(
        summary = "Get customer authentication status",
        description = "Check if customer authentication is enabled for visitor routes"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Customer auth status retrieved successfully",
        content = [Content(schema = Schema(implementation = CustomerAuthStatusResponse::class))]
    )
    fun getCustomerAuthStatus(): ResponseEntity<CustomerAuthStatusResponse> {
        val enabled = appSettingsService.isCustomerAuthEnabled()
        return ResponseEntity.ok(CustomerAuthStatusResponse(enabled))
    }
    
    @PutMapping("/customer-auth")
    @Operation(
        summary = "Enable or disable customer authentication",
        description = "Toggle customer authentication requirement for visitor routes. Requires admin authentication.",
        security = [SecurityRequirement(name = "cookieAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Customer auth setting updated successfully",
                content = [Content(schema = Schema(implementation = CustomerAuthStatusResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - admin authentication required"
            )
        ]
    )
    fun setCustomerAuthStatus(@RequestBody request: CustomerAuthUpdateRequest): ResponseEntity<CustomerAuthStatusResponse> {
        val enabled = appSettingsService.setCustomerAuthEnabled(request.enabled)
        return ResponseEntity.ok(CustomerAuthStatusResponse(enabled))
    }
}
