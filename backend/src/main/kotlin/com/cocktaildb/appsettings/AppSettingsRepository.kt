package com.cocktaildb.appsettings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AppSettingsRepository : JpaRepository<AppSettings, Long> {
    fun findBySettingKey(key: String): Optional<AppSettings>
}