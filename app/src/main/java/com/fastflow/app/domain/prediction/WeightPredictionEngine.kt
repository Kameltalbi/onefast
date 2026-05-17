package com.fastflow.app.domain.prediction

import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.WeightEntry
import com.fastflow.app.domain.model.WeightPrediction
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

@Singleton
class WeightPredictionEngine @Inject constructor() {

    fun predict(
        weightEntries: List<WeightEntry>,
        targetWeightKg: Float?,
        averageFastHours: Float,
        currentStreak: Int,
        totalFastsCompleted: Int
    ): WeightPrediction {
        val sorted = weightEntries.sortedBy { it.timestamp }
        val currentWeight = sorted.lastOrNull()?.getWeightInKg()

        if (currentWeight == null) {
            return emptyPrediction(
                insight = "Ajoutez votre poids pour activer les prédictions personnalisées.",
                recommendedPlan = FastingType.SIXTEEN_EIGHT
            )
        }

        val weeklyRate = calculateWeeklyLossRate(sorted)
        val hasEnoughData = weeklyRate != null && sorted.size >= 2

        val effectiveRate = (weeklyRate ?: DEFAULT_WEEKLY_LOSS_KG).coerceIn(0.15f, 1.2f)
        val target = targetWeightKg?.takeIf { it > 0 && it < currentWeight }
        val recommendedPlan = recommendPlan(averageFastHours, currentStreak, totalFastsCompleted)

        val (weeksToTarget, goalDate) = if (target != null && !hasGoalReached(currentWeight, target)) {
            val kgToLose = currentWeight - target
            val weeks = max(1, (kgToLose / effectiveRate).roundToInt())
            val date = System.currentTimeMillis() + weeks * 7L * 24 * 60 * 60 * 1000
            weeks to date
        } else {
            null to null
        }

        val projected4Weeks = currentWeight - effectiveRate * 4f

        val insight = buildInsight(
            currentWeight = currentWeight,
            target = target,
            weeks = weeksToTarget,
            weeklyRate = effectiveRate,
            hasEnoughData = hasEnoughData,
            recommendedPlan = recommendedPlan,
            isGoalReached = target != null && hasGoalReached(currentWeight, target)
        )

        return WeightPrediction(
            currentWeightKg = currentWeight,
            targetWeightKg = target,
            weeksToTarget = weeksToTarget,
            estimatedGoalDateMillis = goalDate,
            weeklyLossRateKg = effectiveRate,
            projectedWeightIn4Weeks = projected4Weeks.coerceAtLeast(0f),
            recommendedPlan = recommendedPlan,
            insightMessage = insight,
            hasEnoughData = hasEnoughData || target != null
        )
    }

    private fun calculateWeeklyLossRate(entries: List<WeightEntry>): Float? {
        if (entries.size < 2) return null

        val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        val recent = entries.filter { it.timestamp >= thirtyDaysAgo }
        if (recent.size < 2) return null

        val first = recent.first()
        val last = recent.last()
        val days = ((last.timestamp - first.timestamp) / TimeUnit.DAYS.toMillis(1)).coerceAtLeast(1)
        val weightDiff = first.getWeightInKg() - last.getWeightInKg()

        if (weightDiff <= 0) return null

        return (weightDiff / days) * 7f
    }

    private fun recommendPlan(
        averageFastHours: Float,
        streak: Int,
        totalFasts: Int
    ): FastingType {
        return when {
            totalFasts < 3 -> FastingType.FOURTEEN_TEN
            streak >= 14 && averageFastHours >= 18f -> FastingType.TWENTY_FOUR
            streak >= 7 && averageFastHours >= 16f -> FastingType.EIGHTEEN_SIX
            streak >= 3 -> FastingType.SIXTEEN_EIGHT
            else -> FastingType.FOURTEEN_TEN
        }
    }

    private fun hasGoalReached(current: Float, target: Float) = current <= target + 0.2f

    private fun buildInsight(
        currentWeight: Float,
        target: Float?,
        weeks: Int?,
        weeklyRate: Float,
        hasEnoughData: Boolean,
        recommendedPlan: FastingType,
        isGoalReached: Boolean
    ): String {
        if (isGoalReached && target != null) {
            return "Félicitations ! Vous avez atteint votre objectif de ${"%.1f".format(target)} kg."
        }

        if (target != null && weeks != null) {
            val dateStr = formatDate(
                System.currentTimeMillis() + weeks * 7L * 24 * 60 * 60 * 1000
            )
            return if (hasEnoughData) {
                "Avec votre rythme actuel (~${"%.1f".format(weeklyRate)} kg/semaine), " +
                    "vous pourriez atteindre ${"%.1f".format(target)} kg dans environ $weeks semaines ($dateStr). " +
                    "Plan recommandé : ${recommendedPlan.displayName}."
            } else {
                "En visant ~${"%.1f".format(weeklyRate)} kg/semaine, " +
                    "vous pourriez atteindre ${"%.1f".format(target)} kg dans environ $weeks semaines ($dateStr)."
            }
        }

        return if (hasEnoughData) {
            "Vous perdez environ ${"%.1f".format(weeklyRate)} kg par semaine. " +
                "Définissez un objectif de poids pour une projection personnalisée. " +
                "Plan optimal : ${recommendedPlan.displayName}."
        } else {
            "Continuez à enregistrer votre poids chaque semaine pour des prédictions plus précises. " +
                "Plan suggéré pour débuter : ${recommendedPlan.displayName}."
        }
    }

    private fun formatDate(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val months = listOf(
            "jan.", "fév.", "mars", "avr.", "mai", "juin",
            "juil.", "août", "sept.", "oct.", "nov.", "déc."
        )
        return "${cal.get(Calendar.DAY_OF_MONTH)} ${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }

    private fun emptyPrediction(insight: String, recommendedPlan: FastingType) = WeightPrediction(
        currentWeightKg = null,
        targetWeightKg = null,
        weeksToTarget = null,
        estimatedGoalDateMillis = null,
        weeklyLossRateKg = DEFAULT_WEEKLY_LOSS_KG,
        projectedWeightIn4Weeks = null,
        recommendedPlan = recommendedPlan,
        insightMessage = insight,
        hasEnoughData = false
    )

    companion object {
        const val DEFAULT_WEEKLY_LOSS_KG = 0.5f
    }
}
