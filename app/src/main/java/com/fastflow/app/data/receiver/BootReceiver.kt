package com.fastflow.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fastflow.app.di.ReceiverEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ReceiverEntryPoint::class.java
        )
        val fastingRepository = entryPoint.fastingRepository()
        val smartNotificationScheduler = entryPoint.smartNotificationScheduler()

        CoroutineScope(Dispatchers.IO).launch {
            fastingRepository.getCurrentSession()?.let { session ->
                if (session.isActive()) {
                    fastingRepository.syncSessionPhase(session.id)
                    fastingRepository.getCurrentSession()?.let { updated ->
                        smartNotificationScheduler.scheduleForSession(updated)
                    }
                }
            }
        }
    }
}
