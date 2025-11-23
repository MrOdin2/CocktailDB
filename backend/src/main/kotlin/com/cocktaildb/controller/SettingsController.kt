package com.cocktaildb.controller

import com.cocktaildb.service.AppSettingsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class ThemeRequest(val theme: String)
data class ThemeResponse(val theme: String)

@RestController
@RequestMapping("/api/settings")
class SettingsController(
    private val appSettingsService: AppSettingsService
) {
    
    @GetMapping("/theme")
    fun getTheme(): ResponseEntity<ThemeResponse> {
        val theme = appSettingsService.getTheme()
        return ResponseEntity.ok(ThemeResponse(theme))
    }
    
    @PutMapping("/theme")
    fun setTheme(@RequestBody request: ThemeRequest): ResponseEntity<ThemeResponse> {
        // TODO: Add admin authorization check when authentication is implemented
        val theme = appSettingsService.setTheme(request.theme)
        return ResponseEntity.ok(ThemeResponse(theme))
    }
}
