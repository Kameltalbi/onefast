package com.fastflow.app.domain.model

data class WeightPrediction(
    val currentWeightKg: Float?,
    val targetWeightKg: Float?,
    val weeksToTarget: Int?,
    val estimatedGoalDateMillis: Long?,
    val weeklyLossRateKg: Float,
    val projectedWeightIn4Weeks: Float?,
    val recommendedPlan: FastingType,
    val insightMessage: String,
    val hasEnoughData: Boolean
) {
    val isGoalReached: Boolean
        get() = currentWeightKg != null && targetWeightKg != null &&
            currentWeightKg <= targetWeightKg + 0.2f
}
