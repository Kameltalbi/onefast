package com.fastflow.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fastflow.app.data.notification.AlarmScheduler
import com.fastflow.app.data.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmScheduler.ACTION_FASTING_COMPLETE -> {
                notificationHelper.showFastingCompletedNotification()
            }
            AlarmScheduler.ACTION_TWO_HOURS_LEFT -> {
                notificationHelper.showTwoHoursLeftNotification()
            }
            AlarmScheduler.ACTION_FAT_BURN_PHASE -> {
                notificationHelper.showFatBurnPhaseNotification()
            }
            AlarmScheduler.ACTION_HYDRATION_REMINDER -> {
                notificationHelper.showHydrationReminder()
            }
            AlarmScheduler.ACTION_EATING_WINDOW_OPEN -> {
                notificationHelper.showEatingWindowOpenNotification()
            }
            AlarmScheduler.ACTION_RAMADAN_IFTAR -> {
                notificationHelper.showRamadanIftarNotification()
            }
            AlarmScheduler.ACTION_RAMADAN_SUHOOR -> {
                notificationHelper.showRamadanSuhoorNotification()
            }
            AlarmScheduler.ACTION_RAMADAN_HYDRATION -> {
                notificationHelper.showRamadanHydrationNotification()
            }
        }
    }
}
