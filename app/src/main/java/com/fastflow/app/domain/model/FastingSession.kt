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
        val now = System.currentTimeMillis()
        return when (status) {
            FastingStatus.COMPLETED -> endTimeActual?.let { it - startTime - totalPausedDuration } ?: 0
            FastingStatus.FASTING -> now - startTime - totalPausedDuration
            FastingStatus.PAUSED -> pausedAt?.let { it - startTime - totalPausedDuration } ?: 0
            FastingStatus.EATING_WINDOW -> {
                val eatingStart = endTimeActual ?: return 0
                (now - eatingStart).coerceAtLeast(0)
            }
            else -> 0
        }
    }

    fun getProgress(): Float {
        val now = System.currentTimeMillis()
        return when (status) {
            FastingStatus.FASTING, FastingStatus.PAUSED -> {
                val expected = endTimeExpected - startTime
                if (expected <= 0) return 0f
                (getDuration().toFloat() / expected.toFloat()).coerceIn(0f, 1f)
            }
            FastingStatus.EATING_WINDOW -> {
                val eatingStart = endTimeActual ?: return 0f
                val expected = endTimeExpected - eatingStart
                if (expected <= 0) return 0f
                ((now - eatingStart).toFloat() / expected.toFloat()).coerceIn(0f, 1f)
            }
            else -> 0f
        }
    }

    fun getRemainingTime(): Long {
        val now = System.currentTimeMillis()
        return when (status) {
            FastingStatus.FASTING, FastingStatus.PAUSED, FastingStatus.EATING_WINDOW -> {
                (endTimeExpected - now).coerceAtLeast(0)
            }
            else -> 0
        }
    }

    fun getElapsedHours(): Float = getDuration() / 3_600_000f

    fun isActive(): Boolean =
        status == FastingStatus.FASTING ||
            status == FastingStatus.PAUSED ||
            status == FastingStatus.EATING_WINDOW

    fun isFastingPhase(): Boolean =
        status == FastingStatus.FASTING || status == FastingStatus.PAUSED

    fun customFastingHoursOrNull(): Int? =
        if (fastingType == FastingType.CUSTOM) fastingHoursActual else null
}
