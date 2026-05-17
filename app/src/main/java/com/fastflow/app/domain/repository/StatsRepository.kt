package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    suspend fun getUserStats(): UserStats
    fun observeUserStats(): Flow<UserStats>
    suspend fun updateStats(): Result<Unit>
}
