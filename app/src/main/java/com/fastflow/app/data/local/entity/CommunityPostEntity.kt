package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.CommunityGroup
import com.fastflow.app.domain.model.CommunityPost
import com.fastflow.app.domain.model.CommunityPostType

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val authorId: String,
    val authorName: String,
    val type: String,
    val groupTag: String,
    val content: String,
    val createdAt: Long,
    val isHidden: Boolean = false,
    val isReported: Boolean = false,
    val isSeedPost: Boolean = false
) {
    fun toDomain(currentUserId: String): CommunityPost = CommunityPost(
        id = id,
        authorId = authorId,
        authorName = authorName,
        type = CommunityPostType.valueOf(type),
        group = CommunityGroup.valueOf(groupTag),
        content = content,
        createdAt = createdAt,
        isOwnPost = authorId == currentUserId,
        isHidden = isHidden,
        isReported = isReported
    )

    companion object {
        fun fromPost(
            authorId: String,
            authorName: String,
            type: CommunityPostType,
            group: CommunityGroup,
            content: String
        ): CommunityPostEntity = CommunityPostEntity(
            authorId = authorId,
            authorName = authorName,
            type = type.name,
            groupTag = group.name,
            content = content,
            createdAt = System.currentTimeMillis()
        )
    }
}
