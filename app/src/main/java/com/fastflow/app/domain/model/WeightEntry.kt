package com.fastflow.app.domain.model

data class WeightEntry(
    val id: Int = 0,
    val timestamp: Long,
    val weight: Float,
    val unit: WeightUnit = WeightUnit.KG,
    val waistCm: Float? = null
) {
    fun getWeightInKg(): Float {
        return when (unit) {
            WeightUnit.KG -> weight
            WeightUnit.LBS -> weight * 0.453592f
        }
    }

    fun getWeightInLbs(): Float {
        return when (unit) {
            WeightUnit.KG -> weight * 2.20462f
            WeightUnit.LBS -> weight
        }
    }
}

enum class WeightUnit {
    KG,
    LBS
}

object BmiCalculator {
    fun calculate(weightKg: Float, heightCm: Float): Float? {
        if (weightKg <= 0f || heightCm <= 0f) return null
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

    fun category(bmi: Float): String = when {
        bmi < 18.5f -> "Insuffisance pondérale"
        bmi < 25f -> "Poids normal"
        bmi < 30f -> "Surpoids"
        else -> "Obésité"
    }
}
