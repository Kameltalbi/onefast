package com.fastflow.app.domain.util

import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.repository.FastingRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoStartManager @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val fastingRepository: FastingRepository
) {
    suspend fun tryAutoStart(): Boolean {
        if (!preferencesManager.autoStartEnabled.first()) return false
        if (fastingRepository.getCurrentSession() != null) return false

        val todayStart = startOfDayMillis()
        if (preferencesManager.getLastAutoStartDay() >= todayStart) return false

        val hour = preferencesManager.autoStartHour.first()
        val minute = preferencesManager.autoStartMinute.first()
        val now = Calendar.getInstance()
        val scheduledMinutes = hour * 60 + minute
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        if (currentMinutes < scheduledMinutes) return false

        val typeName = preferencesManager.getDefaultFastingTypeOnce()
        val type = typeName?.let { runCatching { FastingType.valueOf(it) }.getOrNull() }
            ?: FastingType.SIXTEEN_EIGHT
        val customHours = if (type == FastingType.CUSTOM) {
            preferencesManager.getCustomFastingHoursOnce()
        } else {
            null
        }

        val result = fastingRepository.startFasting(type, customHours)
        if (result.isSuccess) {
            preferencesManager.setLastAutoStartDay(todayStart)
            return true
        }
        return false
    }

    private fun startOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
