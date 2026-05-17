package com.fastflow.app.domain.usecase.health

import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.health.HealthRiskAnalyzer
import com.fastflow.app.domain.model.HealthAlert
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetHealthAlertsUseCase @Inject constructor(
    private val fastingRepository: FastingRepository,
    private val weightRepository: WeightRepository,
    private val preferencesManager: PreferencesManager,
    private val healthRiskAnalyzer: HealthRiskAnalyzer
) {
    suspend operator fun invoke(): List<HealthAlert> {
        val dismissed = preferencesManager.getDismissedHealthAlertsOnce()
        val weekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val fatigueCount = preferencesManager.getFatigueMentionsSince(weekAgo)

        val alerts = healthRiskAnalyzer.analyze(
            currentSession = fastingRepository.getCurrentSession(),
            completedSessions = fastingRepository.getCompletedSessions(),
            weightEntries = weightRepository.getAllWeightEntries(),
            fatigueMentionsLast7Days = fatigueCount
        )

        return alerts.filter { it.id !in dismissed }
    }
}
