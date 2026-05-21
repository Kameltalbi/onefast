package com.fastflow.app.domain.model

data class WaterEntry(
    val id: Int = 0,
    val timestamp: Long,
    val amountMl: Int
)

data class DailyHydration(
    val totalMl: Int,
    val goalMl: Int,
    val glassSizeMl: Int,
    val entries: List<WaterEntry>
) {
    val glassesConsumed: Int
        get() = if (glassSizeMl > 0) totalMl / glassSizeMl else 0

    val glassesGoal: Int
        get() = if (glassSizeMl > 0) (goalMl + glassSizeMl - 1) / glassSizeMl else 0

    val progress: Float
        get() = if (goalMl > 0) (totalMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f

    val remainingMl: Int
        get() = (goalMl - totalMl).coerceAtLeast(0)
}
