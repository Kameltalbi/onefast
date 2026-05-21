package com.fastflow.app.domain.fast

import com.fastflow.app.domain.model.FastingWeightEstimate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Estimates energy expenditure during intermittent fasting.
 *
 * - No estimate below [MIN_FASTING_HOURS].
 * - First 16 h: moderate expenditure (~40 kcal/h).
 * - Beyond 16 h: higher fat-oxidation estimate (~70 kcal/h).
 * - Weight loss uses ~7700 kcal per kg of body fat.
 */
@Singleton
class FastingBurnEstimator @Inject constructor() {

    fun estimate(fastingHours: Float, currentWeightKg: Float?): FastingWeightEstimate? {
        if (fastingHours < MIN_FASTING_HOURS || currentWeightKg == null) return null
        if (currentWeightKg !in 40f..250f) return null

        val cappedHours = fastingHours.coerceAtMost(MAX_FASTING_HOURS)
        val baseHours = MIN_FASTING_HOURS.coerceAtMost(cappedHours)
        val extraHours = (cappedHours - MIN_FASTING_HOURS).coerceAtLeast(0f)

        val caloriesBurned = (baseHours * KCAL_PER_HOUR_BASE + extraHours * KCAL_PER_HOUR_AFTER_16)
            .roundToInt()
            .coerceAtLeast(1)

        val kgLost = caloriesBurned / KCAL_PER_KG_FAT
        val estimatedWeightKg = (currentWeightKg - kgLost).coerceIn(40f, 250f)

        return FastingWeightEstimate(
            fastingHours = cappedHours,
            caloriesBurned = caloriesBurned,
            kgLost = kgLost,
            previousWeightKg = currentWeightKg,
            estimatedWeightKg = estimatedWeightKg
        )
    }

    companion object {
        const val MIN_FASTING_HOURS = 16f
        private const val MAX_FASTING_HOURS = 72f
        private const val KCAL_PER_HOUR_BASE = 40f
        private const val KCAL_PER_HOUR_AFTER_16 = 70f
        private const val KCAL_PER_KG_FAT = 7700f
    }
}
