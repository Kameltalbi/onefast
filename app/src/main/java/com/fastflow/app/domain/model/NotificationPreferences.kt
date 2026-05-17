package com.fastflow.app.domain.model

data class NotificationPreferences(
    val enabled: Boolean = true,
    val hydrationEnabled: Boolean = true,
    val twoHoursLeftEnabled: Boolean = true,
    val fatBurnEnabled: Boolean = true,
    val streakMilestonesEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = true,
    val quietStartHour: Int = 22,
    val quietEndHour: Int = 7
)
