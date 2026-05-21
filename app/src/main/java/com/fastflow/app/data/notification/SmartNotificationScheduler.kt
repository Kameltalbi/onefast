package com.fastflow.app.data.notification

import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartNotificationScheduler @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val preferencesManager: PreferencesManager
) {
    suspend fun scheduleForSession(session: FastingSession) {
        val prefs = preferencesManager.getNotificationPreferencesOnce()
        if (!prefs.enabled) {
            alarmScheduler.cancelAllAlarms()
            return
        }

        alarmScheduler.cancelAllAlarms()
        val now = System.currentTimeMillis()

        when (session.status) {
            FastingStatus.FASTING, FastingStatus.PAUSED -> {
                scheduleIfFuture(
                    AlarmScheduler.ACTION_FASTING_COMPLETE,
                    AlarmScheduler.REQUEST_CODE_FASTING,
                    session.endTimeExpected,
                    prefs,
                    now
                )
                scheduleHydrationReminders(session.endTimeExpected, prefs, now)
            }
            FastingStatus.EATING_WINDOW -> {
                scheduleIfFuture(
                    AlarmScheduler.ACTION_EATING_WINDOW_END,
                    AlarmScheduler.REQUEST_CODE_EATING,
                    session.endTimeExpected,
                    prefs,
                    now
                )
                scheduleHydrationReminders(session.endTimeExpected, prefs, now)
            }
            else -> Unit
        }
    }

    private fun scheduleHydrationReminders(
        phaseEndMillis: Long,
        prefs: NotificationPreferences,
        now: Long
    ) {
        if (!prefs.hydrationEnabled) return

        val intervalMs = 2 * 60 * 60 * 1000L
        var trigger = now + intervalMs
        var requestCode = 1006

        while (trigger < phaseEndMillis && requestCode <= 1010) {
            scheduleIfFuture(
                AlarmScheduler.ACTION_HYDRATION_REMINDER,
                requestCode,
                trigger,
                prefs,
                now
            )
            trigger += intervalMs
            requestCode++
        }
    }

    private fun scheduleIfFuture(
        action: String,
        requestCode: Int,
        triggerAt: Long,
        prefs: NotificationPreferences,
        now: Long
    ) {
        if (triggerAt <= now) return
        val adjusted = NotificationPolicy.adjustForQuietHours(triggerAt, prefs)
        alarmScheduler.scheduleAlarm(action, requestCode, adjusted)
    }
}
