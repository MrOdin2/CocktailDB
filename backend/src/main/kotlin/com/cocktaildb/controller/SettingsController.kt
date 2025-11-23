package com.cocktaildb.controller

import com.cocktaildb.service.AppSettingsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class ThemeRequest(val theme: String)
data class ThemeResponse(val theme: String)

@RestController
@RequestMapping("/api/settings")
class SettingsController(
    private val appSettingsService: AppSettingsService
) {
    
    companion object {
        val VALID_THEMES = setOf("basic", "terminal-green", "cyberpunk", "amber")
    }
    
    @GetMapping("/theme")
    fun getTheme(): ResponseEntity<ThemeResponse> {
        val theme = appSettingsService.getTheme()
        return ResponseEntity.ok(ThemeResponse(theme))
    }
    
    @PutMapping("/theme")
    fun setTheme(@RequestBody request: ThemeRequest): ResponseEntity<Any> {
        // Validate theme value
        if (!VALID_THEMES.contains(request.theme)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Invalid theme. Must be one of: ${VALID_THEMES.joinToString(", ")}"))
        }
        
        // TODO: Add admin authorization check when authentication is implemented
        val theme = appSettingsService.setTheme(request.theme)
        return ResponseEntity.ok(ThemeResponse(theme))
    }
}
