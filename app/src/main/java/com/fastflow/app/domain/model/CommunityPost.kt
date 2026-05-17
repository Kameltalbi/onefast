package com.fastflow.app.domain.model

enum class CommunityPostType {
    MOTIVATION,
    MEAL_IDEA,
    PROGRESS
}

enum class CommunityGroup {
    GENERAL,
    RAMADAN,
    SUMMER
}

data class CommunityProfile(
    val userId: String,
    val displayName: String,
    val shareAnonymously: Boolean,
    val isSetupComplete: Boolean
)

data class CommunityPost(
    val id: Int,
    val authorId: String,
    val authorName: String,
    val type: CommunityPostType,
    val group: CommunityGroup,
    val content: String,
    val createdAt: Long,
    val isOwnPost: Boolean,
    val isHidden: Boolean,
    val isReported: Boolean
)
