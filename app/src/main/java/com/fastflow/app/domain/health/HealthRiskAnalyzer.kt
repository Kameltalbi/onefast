package com.fastflow.app.domain.health

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.model.HealthAlert
import com.fastflow.app.domain.model.HealthAlertSeverity
import com.fastflow.app.domain.model.HealthRiskType
import com.fastflow.app.domain.model.WeightEntry
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRiskAnalyzer @Inject constructor() {

    fun analyze(
        currentSession: FastingSession?,
        completedSessions: List<FastingSession>,
        weightEntries: List<WeightEntry>,
        fatigueMentionsLast7Days: Int
    ): List<HealthAlert> {
        val alerts = mutableListOf<HealthAlert>()

        checkActiveFastDuration(currentSession)?.let { alerts.add(it) }
        checkExcessiveFrequency(completedSessions, currentSession)?.let { alerts.add(it) }
        alerts.addAll(checkRapidWeightLoss(weightEntries))
        checkUnderweight(weightEntries)?.let { alerts.add(it) }
        checkFatiguePattern(fatigueMentionsLast7Days)?.let { alerts.add(it) }

        return alerts.sortedByDescending { it.severity.ordinal }
    }

    private fun checkActiveFastDuration(session: FastingSession?): HealthAlert? {
        if (session == null || !session.isActive()) return null

        val elapsedHours = session.getElapsedHours()
        val plannedHours = session.fastingHoursActual.toFloat()

        return when {
            elapsedHours >= CRITICAL_FAST_HOURS -> HealthAlert(
                id = HealthRiskType.EXTENDED_ACTIVE_FAST.name,
                type = HealthRiskType.EXTENDED_ACTIVE_FAST,
                severity = HealthAlertSeverity.CRITICAL,
                title = "Jeûne très prolongé",
                message = "Votre jeûne dure depuis ${elapsedHours.toInt()} h. Un jeûne aussi long peut être risqué sans suivi médical."
            )
            elapsedHours >= plannedHours + 2f || elapsedHours >= WARNING_FAST_HOURS -> HealthAlert(
                id = HealthRiskType.EXCESSIVE_FAST_DURATION.name,
                type = HealthRiskType.EXCESSIVE_FAST_DURATION,
                severity = HealthAlertSeverity.WARNING,
                title = "Jeûne prolongé",
                message = "Vous dépassez la durée prévue (${plannedHours.toInt()} h). Pensez à rompre le jeûne si vous ne vous sentez pas bien."
            )
            else -> null
        }
    }

    private fun checkExcessiveFrequency(
        completed: List<FastingSession>,
        current: FastingSession?
    ): HealthAlert? {
        val weekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val recentLongFasts = completed.count { session ->
            session.startTime >= weekAgo && sessionDurationHours(session) >= WARNING_FAST_HOURS
        } + if (current?.isActive() == true && current.getElapsedHours() >= WARNING_FAST_HOURS) 1 else 0

        if (recentLongFasts >= MAX_LONG_FASTS_PER_WEEK) {
            return HealthAlert(
                id = HealthRiskType.EXCESSIVE_FAST_FREQUENCY.name,
                type = HealthRiskType.EXCESSIVE_FAST_FREQUENCY,
                severity = HealthAlertSeverity.WARNING,
                title = "Fréquence élevée",
                message = "Vous avez effectué $recentLongFasts jeûnes longs (≥${WARNING_FAST_HOURS.toInt()} h) cette semaine. Accordez-vous des jours de repos."
            )
        }
        return null
    }

    private fun checkRapidWeightLoss(entries: List<WeightEntry>): List<HealthAlert> {
        val sorted = entries.sortedBy { it.timestamp }
        if (sorted.size < 2) return emptyList()

        val weekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val recent = sorted.filter { it.timestamp >= weekAgo }
        if (recent.size < 2) return emptyList()

        val lossKg = recent.first().getWeightInKg() - recent.last().getWeightInKg()
        if (lossKg <= 0) return emptyList()

        return when {
            lossKg >= CRITICAL_WEEKLY_LOSS_KG -> listOf(
                HealthAlert(
                    id = HealthRiskType.RAPID_WEIGHT_LOSS.name,
                    type = HealthRiskType.RAPID_WEIGHT_LOSS,
                    severity = HealthAlertSeverity.CRITICAL,
                    title = "Perte de poids rapide",
                    message = "Vous avez perdu ${"%.1f".format(lossKg)} kg en 7 jours. Une perte trop rapide peut être dangereuse."
                )
            )
            lossKg >= WARNING_WEEKLY_LOSS_KG -> listOf(
                HealthAlert(
                    id = HealthRiskType.RAPID_WEIGHT_LOSS.name,
                    type = HealthRiskType.RAPID_WEIGHT_LOSS,
                    severity = HealthAlertSeverity.WARNING,
                    title = "Perte de poids soutenue",
                    message = "Vous avez perdu ${"%.1f".format(lossKg)} kg cette semaine. Surveillez votre énergie et votre alimentation."
                )
            )
            else -> emptyList()
        }
    }

    private fun checkUnderweight(entries: List<WeightEntry>): HealthAlert? {
        val current = entries.maxByOrNull { it.timestamp }?.getWeightInKg() ?: return null
        if (current < MIN_SAFE_WEIGHT_KG) {
            return HealthAlert(
                id = HealthRiskType.UNDERWEIGHT_CONCERN.name,
                type = HealthRiskType.UNDERWEIGHT_CONCERN,
                severity = HealthAlertSeverity.WARNING,
                title = "Poids bas",
                message = "Votre poids actuel (${"%.1f".format(current)} kg) est bas. Le jeûne prolongé peut ne pas être adapté."
            )
        }
        return null
    }

    private fun checkFatiguePattern(count: Int): HealthAlert? {
        if (count >= FATIGUE_MENTION_THRESHOLD) {
            return HealthAlert(
                id = HealthRiskType.FATIGUE_PATTERN.name,
                type = HealthRiskType.FATIGUE_PATTERN,
                severity = HealthAlertSeverity.WARNING,
                title = "Fatigue récurrente",
                message = "Vous avez signalé de la fatigue à plusieurs reprises. Réduisez la durée du jeûne ou consultez un professionnel."
            )
        }
        return null
    }

    private fun sessionDurationHours(session: FastingSession): Float {
        val end = session.endTimeActual ?: System.currentTimeMillis()
        return (end - session.startTime - session.totalPausedDuration) / 3_600_000f
    }

    companion object {
        const val WARNING_FAST_HOURS = 20f
        const val CRITICAL_FAST_HOURS = 24f
        const val MAX_LONG_FASTS_PER_WEEK = 6
        const val WARNING_WEEKLY_LOSS_KG = 1.0f
        const val CRITICAL_WEEKLY_LOSS_KG = 1.5f
        const val MIN_SAFE_WEIGHT_KG = 48f
        const val FATIGUE_MENTION_THRESHOLD = 3
    }
}
