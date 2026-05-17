package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeightEntryEntity): Long

    @Update
    suspend fun update(entry: WeightEntryEntity)

    @Delete
    suspend fun delete(entry: WeightEntryEntity)

    @Query("SELECT * FROM weight_entries WHERE id = :id")
    suspend fun getById(id: Int): WeightEntryEntity?

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    suspend fun getAllEntries(): List<WeightEntryEntity>

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    fun observeAllEntries(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEntry(): WeightEntryEntity?

    @Query("SELECT * FROM weight_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getEntriesInRange(startTime: Long, endTime: Long): List<WeightEntryEntity>

    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM weight_entries ORDER BY timestamp ASC LIMIT 1")
    suspend fun getFirstEntry(): WeightEntryEntity?
}
