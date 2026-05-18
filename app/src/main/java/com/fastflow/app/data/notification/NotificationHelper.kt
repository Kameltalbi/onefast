package com.fastflow.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fastflow.app.R
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.NotificationPreferences
import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "fastflow_channel"
        const val NOTIFICATION_ID_FASTING = 1001
        const val NOTIFICATION_ID_EATING = 1002
        const val NOTIFICATION_ID_REMINDER = 1003
        const val NOTIFICATION_ID_SMART = 1004
        const val NOTIFICATION_ID_STREAK = 1005
        const val NOTIFICATION_ID_CHALLENGE = 1006
        const val NOTIFICATION_ID_RAMADAN = 1007
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_desc)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun shouldShow(prefs: NotificationPreferences): Boolean {
        return prefs.enabled && !NotificationPolicy.isQuietHours(System.currentTimeMillis(), prefs)
    }

    private fun prefsBlocking(): NotificationPreferences = runBlocking {
        preferencesManager.getNotificationPreferencesOnce()
    }

    private fun baseNotification(title: String, text: String, priority: Int = NotificationCompat.PRIORITY_HIGH) =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(priority)
            .setContentIntent(activityPendingIntent())
            .setAutoCancel(true)

    private fun activityPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showFastingStartedNotification() {
        val prefs = prefsBlocking()
        if (!prefs.enabled) return
        notificationManager.notify(
            NOTIFICATION_ID_FASTING,
            baseNotification(
                context.getString(R.string.notif_fast_started),
                context.getString(R.string.notif_fast_started_body)
            ).build()
        )
    }

    fun showFastingCompletedNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        notificationManager.notify(
            NOTIFICATION_ID_FASTING,
            baseNotification(
                context.getString(R.string.notif_fast_ended),
                context.getString(R.string.notif_fast_ended_body)
            ).build()
        )
    }

    fun showTwoHoursLeftNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs) || !prefs.twoHoursLeftEnabled) return
        notificationManager.notify(
            NOTIFICATION_ID_SMART,
            baseNotification(
                context.getString(R.string.notif_two_hours_title),
                context.getString(R.string.notif_two_hours_body)
            ).build()
        )
    }

    fun showFatBurnPhaseNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs) || !prefs.fatBurnEnabled) return
        notificationManager.notify(
            NOTIFICATION_ID_SMART,
            baseNotification(
                context.getString(R.string.notif_fat_burn_title),
                context.getString(R.string.notif_fat_burn_body)
            ).build()
        )
    }

    fun showHydrationReminder() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs) || !prefs.hydrationEnabled) return
        notificationManager.notify(
            NOTIFICATION_ID_REMINDER,
            baseNotification(
                context.getString(R.string.notif_hydration),
                context.getString(R.string.notif_hydration_body),
                NotificationCompat.PRIORITY_DEFAULT
            ).build()
        )
    }

    fun showStreakMilestoneNotification(streakDays: Int) {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs) || !prefs.streakMilestonesEnabled) return
        notificationManager.notify(
            NOTIFICATION_ID_STREAK,
            baseNotification(
                context.getString(R.string.notif_streak_title),
                context.getString(R.string.notif_streak_body, streakDays)
            ).build()
        )
    }

    fun showEatingWindowOpenNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        notificationManager.notify(
            NOTIFICATION_ID_EATING,
            baseNotification(
                context.getString(R.string.notif_eating_window_open),
                context.getString(R.string.notif_eating_window_body),
                NotificationCompat.PRIORITY_DEFAULT
            ).build()
        )
    }

    fun showChallengeMilestoneNotification(type: ChallengeType, percent: Int) {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        val name = challengeName(type)
        notificationManager.notify(
            NOTIFICATION_ID_CHALLENGE,
            baseNotification(
                context.getString(R.string.challenge_notif_half_title),
                context.getString(R.string.challenge_notif_half_body, name, percent)
            ).build()
        )
    }

    fun showChallengeCompletedNotification(type: ChallengeType) {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        val name = challengeName(type)
        notificationManager.notify(
            NOTIFICATION_ID_CHALLENGE,
            baseNotification(
                context.getString(R.string.challenge_notif_win_title),
                context.getString(R.string.challenge_notif_win_body, name)
            ).build()
        )
    }

    private fun challengeName(type: ChallengeType): String = when (type) {
        ChallengeType.SEVEN_DAYS -> context.getString(R.string.challenge_7_days_title)
        ChallengeType.THIRTY_DAYS -> context.getString(R.string.challenge_30_days_title)
        ChallengeType.RAMADAN -> context.getString(R.string.challenge_ramadan_title)
        ChallengeType.SUMMER_BODY -> context.getString(R.string.challenge_summer_title)
    }

    fun showRamadanIftarNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        notificationManager.notify(
            NOTIFICATION_ID_RAMADAN,
            baseNotification(
                context.getString(R.string.ramadan_notif_iftar_title),
                context.getString(R.string.ramadan_notif_iftar_body)
            ).build()
        )
    }

    fun showRamadanSuhoorNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        notificationManager.notify(
            NOTIFICATION_ID_RAMADAN,
            baseNotification(
                context.getString(R.string.ramadan_notif_suhoor_title),
                context.getString(R.string.ramadan_notif_suhoor_body)
            ).build()
        )
    }

    fun showRamadanHydrationNotification() {
        val prefs = prefsBlocking()
        if (!shouldShow(prefs)) return
        notificationManager.notify(
            NOTIFICATION_ID_RAMADAN,
            baseNotification(
                context.getString(R.string.ramadan_notif_hydration_title),
                context.getString(R.string.ramadan_notif_hydration_body),
                NotificationCompat.PRIORITY_DEFAULT
            ).build()
        )
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
