package com.fastflow.app.data.repository

import com.fastflow.app.data.local.dao.WaterEntryDao
import com.fastflow.app.data.local.entity.WaterEntryEntity
import com.fastflow.app.domain.history.FastingCalendarBuilder
import com.fastflow.app.domain.model.DailyHydration
import com.fastflow.app.domain.model.WaterEntry
import com.fastflow.app.domain.repository.HydrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HydrationRepositoryImpl @Inject constructor(
    private val dao: WaterEntryDao
) : HydrationRepository {

    override fun observeTodayHydration(goalMl: Int, glassSizeMl: Int): Flow<DailyHydration> {
        val dayStart = FastingCalendarBuilder.startOfDayMillis(System.currentTimeMillis())
        val dayEnd = dayStart + 24 * 60 * 60 * 1000L

        return combine(
            dao.observeEntriesForDay(dayStart, dayEnd),
            dao.observeTotalMlForDay(dayStart, dayEnd)
        ) { entries, total ->
            DailyHydration(
                totalMl = total ?: 0,
                goalMl = goalMl,
                glassSizeMl = glassSizeMl,
                entries = entries.map { it.toDomain() }
            )
        }
    }

    override suspend fun addWater(amountMl: Int, timestamp: Long): Result<WaterEntry> {
        return try {
            val id = dao.insert(
                WaterEntryEntity(timestamp = timestamp, amountMl = amountMl.coerceIn(50, 2000))
            )
            val saved = dao.getById(id.toInt())
            saved?.let { Result.success(it.toDomain()) }
                ?: Result.failure(Exception("Erreur lors de l'ajout"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeEntry(entryId: Int): Result<Unit> {
        return try {
            val entry = dao.getById(entryId) ?: return Result.failure(Exception("Entrée introuvable"))
            dao.delete(entry)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
