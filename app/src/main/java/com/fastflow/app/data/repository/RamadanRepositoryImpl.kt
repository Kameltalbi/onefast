package com.fastflow.app.data.repository

import com.fastflow.app.data.notification.RamadanNotificationScheduler
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.data.ramadan.AladhanApiService
import com.fastflow.app.data.ramadan.RamadanTimingsParser
import com.fastflow.app.domain.model.ramadan.RamadanSettings
import com.fastflow.app.domain.model.ramadan.RamadanTimings
import com.fastflow.app.domain.repository.RamadanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RamadanRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val aladhanApi: AladhanApiService,
    private val timingsParser: RamadanTimingsParser,
    private val ramadanScheduler: RamadanNotificationScheduler
) : RamadanRepository {

    @Volatile
    private var cachedTimings: RamadanTimings? = null

    override fun observeSettings(): Flow<RamadanSettings> =
        preferencesManager.ramadanSettings

    override suspend fun updateSettings(settings: RamadanSettings) {
        preferencesManager.setRamadanSettings(settings)
        if (settings.enabled) {
            refreshTimings()
        } else {
            ramadanScheduler.cancelRamadanAlarms()
            cachedTimings = null
        }
    }

    override suspend fun refreshTimings(): Result<RamadanTimings> {
        val settings = preferencesManager.getRamadanSettingsOnce()
        val apiResult = if (settings.latitude != null && settings.longitude != null) {
            aladhanApi.fetchTimingsByCoordinates(settings.latitude, settings.longitude)
        } else {
            aladhanApi.fetchTimingsByCity(settings.city, settings.country)
        }

        return apiResult.fold(
            onSuccess = { data ->
                timingsParser.parse(data.timings!!, data.date?.hijri).map { timings ->
                    cachedTimings = timings
                    preferencesManager.setRamadanTimingsCache(timings)
                    if (settings.enabled) {
                        ramadanScheduler.scheduleForTimings(
                            timings,
                            settings.hydrationRemindersEnabled
                        )
                    }
                    timings
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun getCachedTimings(): RamadanTimings? {
        cachedTimings?.let { return it }
        return preferencesManager.getRamadanTimingsCacheOnce()?.also { cachedTimings = it }
    }
}
