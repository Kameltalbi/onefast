package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.EarnedBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EarnedBadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: EarnedBadgeEntity)

    @Query("SELECT * FROM earned_badges ORDER BY earnedAt DESC")
    fun observeAll(): Flow<List<EarnedBadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM earned_badges WHERE id = :id)")
    suspend fun exists(id: String): Boolean
}
