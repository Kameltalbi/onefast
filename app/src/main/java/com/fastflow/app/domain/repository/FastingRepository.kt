package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.WeeklyFastingDay
import kotlinx.coroutines.flow.Flow

interface FastingRepository {
    suspend fun startFasting(fastingType: FastingType, customFastingHours: Int? = null): Result<FastingSession>
    suspend fun pauseFasting(sessionId: Int): Result<FastingSession>
    suspend fun resumeFasting(sessionId: Int): Result<FastingSession>
    suspend fun stopFasting(sessionId: Int): Result<FastingSession>
    suspend fun openEatingWindow(sessionId: Int): Result<FastingSession>
    suspend fun completeEatingWindow(sessionId: Int): Result<FastingSession>
    suspend fun syncSessionPhase(sessionId: Int): Result<FastingSession?>
    suspend fun getCurrentSession(): FastingSession?
    fun observeCurrentSession(): Flow<FastingSession?>
    suspend fun getSessionById(id: Int): FastingSession?
    suspend fun getAllSessions(): List<FastingSession>
    suspend fun getCompletedSessions(): List<FastingSession>
    suspend fun getWeeklyFastingStats(): List<WeeklyFastingDay>
    suspend fun deleteSession(sessionId: Int): Result<Unit>
}
