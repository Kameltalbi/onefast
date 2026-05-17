package com.fastflow.app.data.notification

import com.fastflow.app.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakMilestoneNotifier @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val notificationHelper: NotificationHelper
) {
    companion object {
        val MILESTONES = listOf(3, 7, 14, 30)
    }

    suspend fun notifyIfMilestoneReached(currentStreak: Int) {
        val prefs = preferencesManager.getNotificationPreferencesOnce()
        if (!prefs.enabled || !prefs.streakMilestonesEnabled) return
        if (currentStreak !in MILESTONES) return

        val lastNotified = preferencesManager.getLastStreakNotifiedOnce()
        if (currentStreak <= lastNotified) return

        notificationHelper.showStreakMilestoneNotification(currentStreak)
        preferencesManager.setLastStreakNotified(currentStreak)
    }
}
