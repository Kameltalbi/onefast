package com.fastflow.app.di

import com.fastflow.app.data.notification.NotificationHelper
import com.fastflow.app.data.notification.SmartNotificationScheduler
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.util.AutoStartManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReceiverEntryPoint {
    fun notificationHelper(): NotificationHelper
    fun fastingRepository(): FastingRepository
    fun smartNotificationScheduler(): SmartNotificationScheduler
    fun autoStartManager(): AutoStartManager
}
