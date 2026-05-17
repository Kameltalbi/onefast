package com.fastflow.app.data.repository

import com.fastflow.app.data.local.dao.FastingSessionDao
import com.fastflow.app.data.local.dao.WeightEntryDao
import com.fastflow.app.domain.model.UserStats
import com.fastflow.app.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val fastingDao: FastingSessionDao,
    private val weightDao: WeightEntryDao
) : StatsRepository {

    override suspend fun getUserStats(): UserStats {
        val completedSessions = fastingDao.getCompletedSessions()
        val totalCompleted = completedSessions.size
        val totalHours = fastingDao.getTotalHoursFasted() ?: 0f
        
        val currentWeight = weightDao.getLatestEntry()?.weight
        val startWeight = weightDao.getFirstEntry()?.weight
        
        val averageDuration = if (totalCompleted > 0) totalHours / totalCompleted else 0f
        
        val streak = calculateCurrentStreak(completedSessions.map { it.startTime })
        val longestStreak = calculateLongestStreak(completedSessions.map { it.startTime })
        
        val weightLost = if (startWeight != null && currentWeight != null) {
            startWeight - currentWeight
        } else {
            0f
        }

        return UserStats(
            currentStreak = streak,
            longestStreak = longestStreak,
            totalHoursFasted = totalHours,
            totalFastsCompleted = totalCompleted,
            averageFastDuration = averageDuration,
            currentWeight = currentWeight,
            startWeight = startWeight,
            weightLost = weightLost
        )
    }

    override fun observeUserStats(): Flow<UserStats> {
        return combine(
            weightDao.observeAllEntries(),
            fastingDao.observeCurrentSession()
        ) { _, _ ->
            getUserStats()
        }
    }

    override suspend fun updateStats(): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateCurrentStreak(completedDates: List<Long>): Int {
        if (completedDates.isEmpty()) return 0
        
        val sortedDates = completedDates.sortedDescending()
        var streak = 0
        var currentDate = System.currentTimeMillis()
        
        for (date in sortedDates) {
            val daysDiff = ((currentDate - date) / (24 * 60 * 60 * 1000)).toInt()
            if (daysDiff <= 1) {
                streak++
                currentDate = date
            } else {
                break
            }
        }
        
        return streak
    }

    private fun calculateLongestStreak(completedDates: List<Long>): Int {
        if (completedDates.isEmpty()) return 0
        
        val sortedDates = completedDates.sorted()
        var longestStreak = 1
        var currentStreak = 1
        
        for (i in 1 until sortedDates.size) {
            val daysDiff = ((sortedDates[i] - sortedDates[i - 1]) / (24 * 60 * 60 * 1000)).toInt()
            if (daysDiff <= 1) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }
        
        return longestStreak
    }
}
