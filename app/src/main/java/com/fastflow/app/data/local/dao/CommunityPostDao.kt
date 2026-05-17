package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.CommunityPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityPostDao {
    @Insert
    suspend fun insert(entity: CommunityPostEntity): Long

    @Insert
    suspend fun insertAll(entities: List<CommunityPostEntity>)

    @Update
    suspend fun update(entity: CommunityPostEntity)

    @Query("SELECT COUNT(*) FROM community_posts")
    suspend fun count(): Int

    @Query("SELECT * FROM community_posts WHERE isHidden = 0 ORDER BY createdAt DESC")
    fun observeVisiblePosts(): Flow<List<CommunityPostEntity>>

    @Query("SELECT * FROM community_posts WHERE id = :id")
    suspend fun getById(id: Int): CommunityPostEntity?
}
