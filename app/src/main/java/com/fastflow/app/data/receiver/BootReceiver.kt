package com.fastflow.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fastflow.app.data.notification.SmartNotificationScheduler
import com.fastflow.app.domain.repository.FastingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var fastingRepository: FastingRepository

    @Inject
    lateinit var smartNotificationScheduler: SmartNotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                fastingRepository.getCurrentSession()?.let { session ->
                    if (session.isActive()) {
                        smartNotificationScheduler.scheduleForSession(session)
                    }
                }
            }
        }
    }
}
