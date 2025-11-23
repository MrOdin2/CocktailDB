package com.cocktaildb.service

import com.cocktaildb.model.AppSettings
import com.cocktaildb.repository.AppSettingsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AppSettingsService(
    private val appSettingsRepository: AppSettingsRepository
) {
    companion object {
        const val THEME_KEY = "theme"
        const val DEFAULT_THEME = "basic"
        val VALID_THEMES = setOf("basic", "terminal-green", "cyberpunk", "amber")
    }

    @Transactional(readOnly = true)
    fun getTheme(): String {
        return appSettingsRepository.findBySettingKey(THEME_KEY)
            .map { it.settingValue }
            .orElse(DEFAULT_THEME)
    }

    @Transactional
    fun setTheme(theme: String): String {
        if (!VALID_THEMES.contains(theme)) {
            throw IllegalArgumentException("Invalid theme: $theme. Valid themes are: ${VALID_THEMES.joinToString(", ")}")
        }

        val setting = appSettingsRepository.findBySettingKey(THEME_KEY)
            .orElse(AppSettings(settingKey = THEME_KEY, settingValue = theme))
        
        setting.settingValue = theme
        appSettingsRepository.save(setting)
        
        return theme
    }
}
