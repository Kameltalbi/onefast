package com.fastflow.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fastflow.app.data.notification.AlarmScheduler
import com.fastflow.app.data.notification.NotificationHelper
import com.fastflow.app.data.notification.SmartNotificationScheduler
import com.fastflow.app.di.ReceiverEntryPoint
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.util.AutoStartManager
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ReceiverEntryPoint::class.java
        )
        val notificationHelper = entryPoint.notificationHelper()
        val fastingRepository = entryPoint.fastingRepository()
        val smartNotificationScheduler = entryPoint.smartNotificationScheduler()
        val autoStartManager = entryPoint.autoStartManager()

        when (intent.action) {
            AlarmScheduler.ACTION_FASTING_COMPLETE -> {
                handleFastingComplete(
                    fastingRepository,
                    notificationHelper,
                    smartNotificationScheduler
                )
            }
            AlarmScheduler.ACTION_EATING_WINDOW_END -> {
                handleEatingWindowEnd(
                    fastingRepository,
                    notificationHelper,
                    autoStartManager
                )
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

    private fun handleFastingComplete(
        fastingRepository: FastingRepository,
        notificationHelper: NotificationHelper,
        smartNotificationScheduler: SmartNotificationScheduler
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val session = fastingRepository.getCurrentSession()
                if (session != null && session.isFastingPhase()) {
                    fastingRepository.openEatingWindow(session.id)
                        .onSuccess { eatingSession ->
                            notificationHelper.showFastingCompletedNotification()
                            notificationHelper.showEatingWindowOpenNotification()
                            smartNotificationScheduler.scheduleForSession(eatingSession)
                        }
                } else {
                    notificationHelper.showFastingCompletedNotification()
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleEatingWindowEnd(
        fastingRepository: FastingRepository,
        notificationHelper: NotificationHelper,
        autoStartManager: AutoStartManager
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val session = fastingRepository.getCurrentSession()
                if (session?.status == FastingStatus.EATING_WINDOW) {
                    fastingRepository.completeEatingWindow(session.id)
                }
                notificationHelper.showEatingWindowCloseNotification()
                autoStartManager.tryAutoStart()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
