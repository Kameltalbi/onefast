package com.fastflow.app.domain.model

data class FastingSession(
    val id: Int = 0,
    val startTime: Long,
    val endTimeExpected: Long,
    val endTimeActual: Long? = null,
    val fastingType: FastingType,
    val fastingHoursActual: Int,
    val status: FastingStatus,
    val pausedAt: Long? = null,
    val totalPausedDuration: Long = 0
) {
    fun getDuration(): Long {
        return when (status) {
            FastingStatus.COMPLETED -> endTimeActual?.let { it - startTime - totalPausedDuration } ?: 0
            FastingStatus.FASTING -> System.currentTimeMillis() - startTime - totalPausedDuration
            FastingStatus.PAUSED -> pausedAt?.let { it - startTime - totalPausedDuration } ?: 0
            else -> 0
        }
    }

    fun getProgress(): Float {
        val duration = getDuration()
        val expected = endTimeExpected - startTime
        return if (expected > 0) (duration.toFloat() / expected.toFloat()).coerceIn(0f, 1f) else 0f
    }

    fun getRemainingTime(): Long {
        return when (status) {
            FastingStatus.FASTING -> {
                val remaining = endTimeExpected - System.currentTimeMillis()
                remaining.coerceAtLeast(0)
            }
            else -> 0
        }
    }

    fun getElapsedHours(): Float = getDuration() / 3_600_000f

    fun isActive(): Boolean = status == FastingStatus.FASTING || status == FastingStatus.PAUSED
}
