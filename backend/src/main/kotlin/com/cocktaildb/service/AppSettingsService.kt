package com.cocktaildb.service

import com.cocktaildb.model.AppSettings
import com.cocktaildb.repository.AppSettingsRepository
import org.springframework.stereotype.Service

@Service
class AppSettingsService(
    private val appSettingsRepository: AppSettingsRepository
) {
    companion object {
        const val THEME_KEY = "theme"
        const val DEFAULT_THEME = "basic"
    }
    
    fun getTheme(): String {
        return appSettingsRepository.findBySettingKey(THEME_KEY)
            .map { it.settingValue }
            .orElse(DEFAULT_THEME)
    }
    
    fun setTheme(theme: String): String {
        val setting = appSettingsRepository.findBySettingKey(THEME_KEY)
            .orElse(AppSettings(settingKey = THEME_KEY, settingValue = theme))
        
        setting.settingValue = theme
        appSettingsRepository.save(setting)
        return theme
    }
    
    fun getSetting(key: String, defaultValue: String = ""): String {
        return appSettingsRepository.findBySettingKey(key)
            .map { it.settingValue }
            .orElse(defaultValue)
    }
    
    fun setSetting(key: String, value: String): String {
        val setting = appSettingsRepository.findBySettingKey(key)
            .orElse(AppSettings(settingKey = key, settingValue = value))
        
        setting.settingValue = value
        appSettingsRepository.save(setting)
        return value
    }
}
