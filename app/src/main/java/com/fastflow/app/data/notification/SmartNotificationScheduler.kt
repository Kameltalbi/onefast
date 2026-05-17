package com.fastflow.app.data.notification

import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** V1 : uniquement notification de fin de jeûne (début géré à l'action utilisateur). */
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

        scheduleIfFuture(
            AlarmScheduler.ACTION_FASTING_COMPLETE,
            AlarmScheduler.REQUEST_CODE_FASTING,
            session.endTimeExpected,
            prefs,
            now
        )
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
