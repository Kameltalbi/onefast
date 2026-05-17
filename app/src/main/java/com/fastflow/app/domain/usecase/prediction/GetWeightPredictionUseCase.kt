package com.fastflow.app.domain.usecase.prediction

import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.WeightPrediction
import com.fastflow.app.domain.prediction.WeightPredictionEngine
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.repository.StatsRepository
import com.fastflow.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetWeightPredictionUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
    private val fastingRepository: FastingRepository,
    private val statsRepository: StatsRepository,
    private val preferencesManager: PreferencesManager,
    private val predictionEngine: WeightPredictionEngine
) {
    suspend operator fun invoke(): WeightPrediction {
        val entries = weightRepository.getAllWeightEntries()
        val stats = statsRepository.getUserStats()
        val targetWeight = preferencesManager.targetWeightKg.first()

        val completed = fastingRepository.getCompletedSessions()
        val avgFastHours = if (completed.isNotEmpty()) {
            completed.map { session ->
                val end = session.endTimeActual ?: session.endTimeExpected
                (end - session.startTime - session.totalPausedDuration) / 3_600_000f
            }.average().toFloat()
        } else {
            0f
        }

        return predictionEngine.predict(
            weightEntries = entries,
            targetWeightKg = targetWeight,
            averageFastHours = avgFastHours,
            currentStreak = stats.currentStreak,
            totalFastsCompleted = stats.totalFastsCompleted
        )
    }
}
