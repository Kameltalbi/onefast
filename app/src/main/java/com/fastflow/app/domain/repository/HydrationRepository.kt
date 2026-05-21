package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.DailyHydration
import com.fastflow.app.domain.model.WaterEntry
import kotlinx.coroutines.flow.Flow

interface HydrationRepository {
    fun observeTodayHydration(goalMl: Int, glassSizeMl: Int): Flow<DailyHydration>
    suspend fun addWater(amountMl: Int, timestamp: Long = System.currentTimeMillis()): Result<WaterEntry>
    suspend fun removeEntry(entryId: Int): Result<Unit>
}
