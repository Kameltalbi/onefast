package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.UserChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserChallengeDao {
    @Insert
    suspend fun insert(entity: UserChallengeEntity): Long

    @Update
    suspend fun update(entity: UserChallengeEntity)

    @Query("SELECT * FROM user_challenges ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<UserChallengeEntity>>

    @Query("SELECT * FROM user_challenges WHERE status = 'ACTIVE'")
    suspend fun getActive(): List<UserChallengeEntity>

    @Query("SELECT * FROM user_challenges WHERE challengeType = :type AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveByType(type: String): UserChallengeEntity?

    @Query("SELECT * FROM user_challenges WHERE id = :id")
    suspend fun getById(id: Int): UserChallengeEntity?
}
