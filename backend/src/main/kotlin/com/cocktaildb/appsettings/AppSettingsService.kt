package com.cocktaildb.appsettings

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
        
        const val CUSTOMER_AUTH_ENABLED_KEY = "customer_auth_enabled"
        const val DEFAULT_CUSTOMER_AUTH_ENABLED = "true"
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
    
    @Transactional(readOnly = true)
    fun isCustomerAuthEnabled(): Boolean {
        return appSettingsRepository.findBySettingKey(CUSTOMER_AUTH_ENABLED_KEY)
            .map { it.settingValue.toBoolean() }
            .orElse(DEFAULT_CUSTOMER_AUTH_ENABLED.toBoolean())
    }
    
    @Transactional
    fun setCustomerAuthEnabled(enabled: Boolean): Boolean {
        val setting = appSettingsRepository.findBySettingKey(CUSTOMER_AUTH_ENABLED_KEY)
            .orElse(AppSettings(settingKey = CUSTOMER_AUTH_ENABLED_KEY, settingValue = enabled.toString()))
        
        setting.settingValue = enabled.toString()
        appSettingsRepository.save(setting)
        
        return enabled
    }
}