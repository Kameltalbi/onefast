package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.FastingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FastingSessionEntity): Long

    @Update
    suspend fun update(session: FastingSessionEntity)

    @Delete
    suspend fun delete(session: FastingSessionEntity)

    @Query("SELECT * FROM fasting_sessions WHERE id = :id")
    suspend fun getById(id: Int): FastingSessionEntity?

    @Query("SELECT * FROM fasting_sessions WHERE status IN ('FASTING', 'PAUSED') ORDER BY startTime DESC LIMIT 1")
    suspend fun getCurrentSession(): FastingSessionEntity?

    @Query("SELECT * FROM fasting_sessions WHERE status IN ('FASTING', 'PAUSED') ORDER BY startTime DESC LIMIT 1")
    fun observeCurrentSession(): Flow<FastingSessionEntity?>

    @Query("SELECT * FROM fasting_sessions ORDER BY startTime DESC")
    suspend fun getAllSessions(): List<FastingSessionEntity>

    @Query("SELECT * FROM fasting_sessions WHERE status = 'COMPLETED' ORDER BY startTime DESC")
    suspend fun getCompletedSessions(): List<FastingSessionEntity>

    @Query("SELECT * FROM fasting_sessions WHERE status = 'COMPLETED' AND startTime >= :startTime ORDER BY startTime DESC")
    suspend fun getCompletedSessionsSince(startTime: Long): List<FastingSessionEntity>

    @Query("DELETE FROM fasting_sessions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM fasting_sessions WHERE status = 'COMPLETED'")
    suspend fun getTotalCompletedCount(): Int

    @Query("SELECT SUM((endTimeActual - startTime - totalPausedDuration) / 3600000.0) FROM fasting_sessions WHERE status = 'COMPLETED'")
    suspend fun getTotalHoursFasted(): Float?
}
