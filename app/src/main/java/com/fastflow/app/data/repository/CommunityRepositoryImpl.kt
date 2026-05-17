package com.fastflow.app.data.repository

import com.fastflow.app.data.local.dao.CommunityPostDao
import com.fastflow.app.data.local.entity.CommunityPostEntity
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.community.CommunitySeedData
import com.fastflow.app.domain.model.*
import com.fastflow.app.domain.repository.CommunityFeedFilter
import com.fastflow.app.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val postDao: CommunityPostDao,
    private val preferencesManager: PreferencesManager
) : CommunityRepository {

    override fun observeProfile(): Flow<CommunityProfile> = preferencesManager.communityProfile

    override suspend fun updateProfile(displayName: String, shareAnonymously: Boolean) {
        preferencesManager.updateCommunityProfile(displayName, shareAnonymously)
    }

    override fun observeFeed(filter: CommunityFeedFilter): Flow<List<CommunityPost>> =
        combine(
            postDao.observeVisiblePosts(),
            preferencesManager.communityProfile
        ) { posts, profile ->
            posts
                .map { it.toDomain(profile.userId) }
                .filter { matchesFilter(it, filter) }
        }

    override suspend fun ensureSeeded() {
        preferencesManager.getOrCreateCommunityUserId()
        if (postDao.count() == 0) {
            postDao.insertAll(CommunitySeedData.posts())
        }
    }

    override suspend fun createPost(
        type: CommunityPostType,
        group: CommunityGroup,
        content: String
    ): Result<Unit> {
        return try {
            val trimmed = content.trim()
            if (trimmed.length < 3) {
                return Result.failure(Exception("Message trop court"))
            }
            val profile = preferencesManager.getCommunityProfileOnce()
            if (!profile.isSetupComplete) {
                return Result.failure(Exception("Configurez votre profil communauté d'abord"))
            }
            val userId = preferencesManager.getOrCreateCommunityUserId()
            val authorName = displayName(profile)
            postDao.insert(
                CommunityPostEntity.fromPost(
                    authorId = userId,
                    authorName = authorName,
                    type = type,
                    group = group,
                    content = trimmed
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun shareProgress(stats: UserStats, group: CommunityGroup): Result<Unit> {
        val progress = stats.getWeightProgress()
        val content = buildString {
            append("Série actuelle : ${stats.currentStreak} j · ")
            append("${stats.totalFastsCompleted} jeûnes terminés")
            if (progress > 0.5f) {
                append(" · ")
                append(String.format("%.1f", progress))
                append(" % de progression poids")
            }
            append(".")
        }
        return createPost(CommunityPostType.PROGRESS, group, content)
    }

    override suspend fun hidePost(postId: Int): Result<Unit> {
        return try {
            val entity = postDao.getById(postId) ?: return Result.failure(Exception("Publication introuvable"))
            postDao.update(entity.copy(isHidden = true))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportPost(postId: Int): Result<Unit> {
        return try {
            val entity = postDao.getById(postId) ?: return Result.failure(Exception("Publication introuvable"))
            postDao.update(entity.copy(isReported = true, isHidden = true))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun displayName(profile: CommunityProfile): String {
        if (profile.shareAnonymously) return "Membre anonyme"
        return profile.displayName.ifBlank { "Membre OneFast" }
    }

    private fun matchesFilter(post: CommunityPost, filter: CommunityFeedFilter): Boolean =
        when (filter) {
            CommunityFeedFilter.ALL -> true
            CommunityFeedFilter.MOTIVATION -> post.type == CommunityPostType.MOTIVATION
            CommunityFeedFilter.MEAL_IDEA -> post.type == CommunityPostType.MEAL_IDEA
            CommunityFeedFilter.PROGRESS -> post.type == CommunityPostType.PROGRESS
        }
}
