package com.fastflow.app.domain.usecase.stats

import com.fastflow.app.domain.model.UserStats
import com.fastflow.app.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserStatsUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<UserStats> {
        return repository.observeUserStats()
    }
}
