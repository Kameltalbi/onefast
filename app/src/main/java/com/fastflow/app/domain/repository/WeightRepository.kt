package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    suspend fun addWeightEntry(
        weight: Float,
        timestamp: Long = System.currentTimeMillis(),
        waistCm: Float? = null
    ): Result<WeightEntry>
    suspend fun updateWeightEntry(entry: WeightEntry): Result<WeightEntry>
    suspend fun deleteWeightEntry(entryId: Int): Result<Unit>
    suspend fun getWeightEntry(id: Int): WeightEntry?
    suspend fun getAllWeightEntries(): List<WeightEntry>
    fun observeWeightEntries(): Flow<List<WeightEntry>>
    suspend fun getLatestWeight(): WeightEntry?
    suspend fun getWeightEntriesInRange(startTime: Long, endTime: Long): List<WeightEntry>
}
