package com.fastflow.app.data.coach

import com.fastflow.app.domain.model.CoachContext
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.repository.StatsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachContextBuilder @Inject constructor(
    private val fastingRepository: FastingRepository,
    private val statsRepository: StatsRepository
) {
    suspend fun build(): CoachContext {
        val session = fastingRepository.getCurrentSession()
        val stats = statsRepository.getUserStats()

        val (status, elapsed, remaining) = when {
            session == null -> Triple("Aucun jeûne en cours", 0f, 0f)
            session.status == FastingStatus.PAUSED -> Triple(
                "Jeûne en pause",
                session.getElapsedHours(),
                session.getRemainingTime() / 3_600_000f
            )
            session.isActive() -> Triple(
                "Jeûne actif",
                session.getElapsedHours(),
                session.getRemainingTime() / 3_600_000f
            )
            else -> Triple("Aucun jeûne en cours", 0f, 0f)
        }

        return CoachContext(
            fastingPlan = session?.fastingType?.displayName ?: "Non défini",
            fastingStatus = status,
            elapsedHours = elapsed,
            remainingHours = remaining,
            currentWeightKg = stats.currentWeight,
            weightLostKg = stats.weightLost.coerceAtLeast(0f),
            currentStreak = stats.currentStreak,
            totalFastsCompleted = stats.totalFastsCompleted
        )
    }
}
