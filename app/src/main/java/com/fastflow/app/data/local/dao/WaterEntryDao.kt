package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.WaterEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WaterEntryEntity): Long

    @Delete
    suspend fun delete(entry: WaterEntryEntity)

    @Query("SELECT * FROM water_entries WHERE id = :id")
    suspend fun getById(id: Int): WaterEntryEntity?

    @Query("SELECT * FROM water_entries WHERE timestamp >= :dayStart AND timestamp < :dayEnd ORDER BY timestamp DESC")
    fun observeEntriesForDay(dayStart: Long, dayEnd: Long): Flow<List<WaterEntryEntity>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_entries WHERE timestamp >= :dayStart AND timestamp < :dayEnd")
    fun observeTotalMlForDay(dayStart: Long, dayEnd: Long): Flow<Int?>
}
