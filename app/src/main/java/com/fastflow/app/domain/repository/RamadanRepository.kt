package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.ramadan.RamadanSettings
import com.fastflow.app.domain.model.ramadan.RamadanTimings
import kotlinx.coroutines.flow.Flow

interface RamadanRepository {
    fun observeSettings(): Flow<RamadanSettings>
    suspend fun updateSettings(settings: RamadanSettings)
    suspend fun refreshTimings(): Result<RamadanTimings>
    suspend fun getCachedTimings(): RamadanTimings?
}
