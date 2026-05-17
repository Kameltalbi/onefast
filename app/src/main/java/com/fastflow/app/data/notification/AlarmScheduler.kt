package com.fastflow.app.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.fastflow.app.data.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_FASTING_COMPLETE = "com.fastflow.app.FASTING_COMPLETE"
        const val ACTION_TWO_HOURS_LEFT = "com.fastflow.app.TWO_HOURS_LEFT"
        const val ACTION_FAT_BURN_PHASE = "com.fastflow.app.FAT_BURN_PHASE"
        const val ACTION_HYDRATION_REMINDER = "com.fastflow.app.HYDRATION_REMINDER"
        const val ACTION_EATING_WINDOW_OPEN = "com.fastflow.app.EATING_WINDOW_OPEN"
        const val ACTION_RAMADAN_IFTAR = "com.fastflow.app.RAMADAN_IFTAR"
        const val ACTION_RAMADAN_SUHOOR = "com.fastflow.app.RAMADAN_SUHOOR"
        const val ACTION_RAMADAN_HYDRATION = "com.fastflow.app.RAMADAN_HYDRATION"

        const val REQUEST_CODE_FASTING = 1001
        const val REQUEST_CODE_EATING = 1002
        const val REQUEST_CODE_HYDRATION = 1003
        const val REQUEST_CODE_TWO_HOURS = 1004
        const val REQUEST_CODE_FAT_BURN = 1005
        const val REQUEST_CODE_RAMADAN_IFTAR = 1020
        const val REQUEST_CODE_RAMADAN_SUHOOR = 1021
        const val REQUEST_CODE_RAMADAN_HYDRATION_1 = 1022
        const val REQUEST_CODE_RAMADAN_HYDRATION_2 = 1023
        const val REQUEST_CODE_RAMADAN_HYDRATION_3 = 1024
        const val REQUEST_CODE_RAMADAN_HYDRATION_4 = 1025
    }

    fun scheduleAlarm(action: String, requestCode: Int, triggerAtMillis: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelAllAlarms() {
        (1001..1010).forEach { requestCode ->
            cancelAlarm(requestCode, ACTION_FASTING_COMPLETE)
            cancelAlarm(requestCode, ACTION_TWO_HOURS_LEFT)
            cancelAlarm(requestCode, ACTION_FAT_BURN_PHASE)
            cancelAlarm(requestCode, ACTION_HYDRATION_REMINDER)
            cancelAlarm(requestCode, ACTION_EATING_WINDOW_OPEN)
        }
        cancelRamadanAlarms()
    }

    fun cancelRamadanAlarms() {
        listOf(
            REQUEST_CODE_RAMADAN_IFTAR to ACTION_RAMADAN_IFTAR,
            REQUEST_CODE_RAMADAN_SUHOOR to ACTION_RAMADAN_SUHOOR,
            REQUEST_CODE_RAMADAN_HYDRATION_1 to ACTION_RAMADAN_HYDRATION,
            REQUEST_CODE_RAMADAN_HYDRATION_2 to ACTION_RAMADAN_HYDRATION,
            REQUEST_CODE_RAMADAN_HYDRATION_3 to ACTION_RAMADAN_HYDRATION,
            REQUEST_CODE_RAMADAN_HYDRATION_4 to ACTION_RAMADAN_HYDRATION
        ).forEach { (code, action) -> cancelAlarm(code, action) }
    }

    private fun cancelAlarm(requestCode: Int, action: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
