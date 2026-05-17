package com.fastflow.app.domain.usecase.fasting

import com.fastflow.app.domain.model.WeeklyFastingDay
import com.fastflow.app.domain.repository.FastingRepository
import javax.inject.Inject

class GetWeeklyFastingStatsUseCase @Inject constructor(
    private val repository: FastingRepository
) {
    suspend operator fun invoke(): List<WeeklyFastingDay> = repository.getWeeklyFastingStats()
}
