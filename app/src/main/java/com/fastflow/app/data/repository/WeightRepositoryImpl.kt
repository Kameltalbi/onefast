package com.fastflow.app.data.repository

import com.fastflow.app.data.local.dao.WeightEntryDao
import com.fastflow.app.data.local.entity.WeightEntryEntity
import com.fastflow.app.domain.model.WeightEntry
import com.fastflow.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeightRepositoryImpl @Inject constructor(
    private val dao: WeightEntryDao
) : WeightRepository {

    override suspend fun addWeightEntry(
        weight: Float,
        timestamp: Long,
        waistCm: Float?
    ): Result<WeightEntry> {
        return try {
            val entry = WeightEntryEntity(
                timestamp = timestamp,
                weight = weight,
                waistCm = waistCm
            )
            val id = dao.insert(entry)
            val savedEntry = dao.getById(id.toInt())

            savedEntry?.let {
                Result.success(it.toDomain())
            } ?: Result.failure(Exception("Erreur lors de l'ajout du poids"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWeightEntry(entry: WeightEntry): Result<WeightEntry> {
        return try {
            val entity = WeightEntryEntity.fromDomain(entry)
            dao.update(entity)
            Result.success(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWeightEntry(entryId: Int): Result<Unit> {
        return try {
            dao.deleteById(entryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeightEntry(id: Int): WeightEntry? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getAllWeightEntries(): List<WeightEntry> {
        return dao.getAllEntries().map { it.toDomain() }
    }

    override fun observeWeightEntries(): Flow<List<WeightEntry>> {
        return dao.observeAllEntries().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getLatestWeight(): WeightEntry? {
        return dao.getLatestEntry()?.toDomain()
    }

    override suspend fun getWeightEntriesInRange(startTime: Long, endTime: Long): List<WeightEntry> {
        return dao.getEntriesInRange(startTime, endTime).map { it.toDomain() }
    }
}
