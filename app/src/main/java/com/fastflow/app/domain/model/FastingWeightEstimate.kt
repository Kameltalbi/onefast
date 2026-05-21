package com.fastflow.app.domain.model

data class FastingWeightEstimate(
    val fastingHours: Float,
    val caloriesBurned: Int,
    val kgLost: Float,
    val previousWeightKg: Float,
    val estimatedWeightKg: Float
)
