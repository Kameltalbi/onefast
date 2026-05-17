package com.fastflow.app.data.notification

import com.fastflow.app.domain.model.NotificationPreferences
import java.util.Calendar

object NotificationPolicy {

    fun isQuietHours(timeMillis: Long, prefs: NotificationPreferences): Boolean {
        if (!prefs.quietHoursEnabled) return false
        return isHourInQuietRange(
            hourOfDay(timeMillis),
            prefs.quietStartHour,
            prefs.quietEndHour
        )
    }

    fun adjustForQuietHours(timeMillis: Long, prefs: NotificationPreferences): Long {
        if (!isQuietHours(timeMillis, prefs)) return timeMillis
        return nextAllowedTime(timeMillis, prefs.quietEndHour)
    }

    fun isHourInQuietRange(hour: Int, startHour: Int, endHour: Int): Boolean {
        return if (startHour > endHour) {
            hour >= startHour || hour < endHour
        } else {
            hour >= startHour && hour < endHour
        }
    }

    private fun hourOfDay(timeMillis: Long): Int {
        return Calendar.getInstance().apply { timeInMillis = timeMillis }
            .get(Calendar.HOUR_OF_DAY)
    }

    private fun nextAllowedTime(fromMillis: Long, quietEndHour: Int): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = fromMillis }
        cal.set(Calendar.HOUR_OF_DAY, quietEndHour)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis <= fromMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }
}
