package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun observeProfile(): Flow<CommunityProfile>
    suspend fun updateProfile(displayName: String, shareAnonymously: Boolean)
    fun observeFeed(filter: CommunityFeedFilter): Flow<List<CommunityPost>>
    suspend fun ensureSeeded()
    suspend fun createPost(type: CommunityPostType, group: CommunityGroup, content: String): Result<Unit>
    suspend fun shareProgress(stats: UserStats, group: CommunityGroup): Result<Unit>
    suspend fun hidePost(postId: Int): Result<Unit>
    suspend fun reportPost(postId: Int): Result<Unit>
}

enum class CommunityFeedFilter {
    ALL,
    MOTIVATION,
    MEAL_IDEA,
    PROGRESS
}
