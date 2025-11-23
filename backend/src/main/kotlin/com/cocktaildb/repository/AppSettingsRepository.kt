package com.cocktaildb.repository

import com.cocktaildb.model.AppSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppSettingsRepository : JpaRepository<AppSettings, Long> {
    fun findBySettingKey(settingKey: String): Optional<AppSettings>
}
