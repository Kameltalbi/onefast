package com.fastflow.app.domain.model

enum class HealthRiskType {
    EXCESSIVE_FAST_DURATION,
    EXCESSIVE_FAST_FREQUENCY,
    RAPID_WEIGHT_LOSS,
    UNDERWEIGHT_CONCERN,
    FATIGUE_PATTERN,
    EXTENDED_ACTIVE_FAST
}

enum class HealthAlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}

data class HealthAlert(
    val id: String,
    val type: HealthRiskType,
    val severity: HealthAlertSeverity,
    val title: String,
    val message: String,
    val recommendation: String = "En cas de doute, consultez un professionnel de santé."
)
