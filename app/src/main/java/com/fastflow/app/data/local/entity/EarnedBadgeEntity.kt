package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.domain.model.EarnedBadge

@Entity(tableName = "earned_badges")
data class EarnedBadgeEntity(
    @PrimaryKey
    val id: String,
    val challengeType: String,
    val earnedAt: Long
) {
    fun toDomain(): EarnedBadge = EarnedBadge(
        id = id,
        challengeType = ChallengeType.valueOf(challengeType),
        earnedAt = earnedAt
    )

    companion object {
        fun fromDomain(badge: EarnedBadge): EarnedBadgeEntity = EarnedBadgeEntity(
            id = badge.id,
            challengeType = badge.challengeType.name,
            earnedAt = badge.earnedAt
        )
    }
}
