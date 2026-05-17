package com.fastflow.app.domain.model

data class CoachContext(
    val fastingPlan: String,
    val fastingStatus: String,
    val elapsedHours: Float,
    val remainingHours: Float,
    val currentWeightKg: Float?,
    val weightLostKg: Float,
    val currentStreak: Int,
    val totalFastsCompleted: Int
) {
    fun toPromptBlock(): String = buildString {
        appendLine("Plan de jeûne: $fastingPlan")
        appendLine("Statut: $fastingStatus")
        if (fastingStatus != "Aucun jeûne en cours") {
            appendLine("Temps écoulé: ${"%.1f".format(elapsedHours)} h")
            appendLine("Temps restant: ${"%.1f".format(remainingHours)} h")
        }
        currentWeightKg?.let { appendLine("Poids actuel: ${"%.1f".format(it)} kg") }
        if (weightLostKg > 0) appendLine("Perte de poids: ${"%.1f".format(weightLostKg)} kg")
        appendLine("Série actuelle: $currentStreak jours")
        appendLine("Jeûnes terminés: $totalFastsCompleted")
    }
}
