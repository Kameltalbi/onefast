package com.fastflow.app.domain.model

enum class HealthConnectStatus {
    AVAILABLE,
    NOT_INSTALLED,
    UPDATE_REQUIRED,
    NOT_SUPPORTED
}

data class HealthSyncSnapshot(
    val status: HealthConnectStatus = HealthConnectStatus.NOT_SUPPORTED,
    val hasPermissions: Boolean = false,
    val stepsToday: Long = 0,
    val stepsWeekAvg: Long = 0,
    val sleepLastNightHours: Float? = null,
    val restingHeartRate: Int? = null,
    val activeCaloriesToday: Double = 0.0,
    val weightFromHealthKg: Float? = null,
    val lastSyncAt: Long? = null,
    val writeWeightEnabled: Boolean = true
)
