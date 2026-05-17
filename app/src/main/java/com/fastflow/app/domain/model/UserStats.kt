package com.fastflow.app.domain.model

data class UserStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalHoursFasted: Float = 0f,
    val totalFastsCompleted: Int = 0,
    val averageFastDuration: Float = 0f,
    val currentWeight: Float? = null,
    val startWeight: Float? = null,
    val weightLost: Float = 0f
) {
    fun getWeightProgress(): Float {
        return if (startWeight != null && currentWeight != null && startWeight > 0) {
            ((startWeight - currentWeight) / startWeight) * 100
        } else {
            0f
        }
    }
}
