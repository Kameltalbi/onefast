package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.ChallengeStatus
import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.domain.model.UserChallenge

@Entity(tableName = "user_challenges")
data class UserChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val challengeType: String,
    val startedAt: Long,
    val status: String,
    val completedDays: Int,
    val lastNotifiedMilestone: Int
) {
    fun toDomain(): UserChallenge = UserChallenge(
        id = id,
        type = ChallengeType.valueOf(challengeType),
        startedAt = startedAt,
        status = ChallengeStatus.valueOf(status),
        completedDays = completedDays,
        lastNotifiedMilestone = lastNotifiedMilestone
    )

    companion object {
        fun fromDomain(challenge: UserChallenge): UserChallengeEntity = UserChallengeEntity(
            id = challenge.id,
            challengeType = challenge.type.name,
            startedAt = challenge.startedAt,
            status = challenge.status.name,
            completedDays = challenge.completedDays,
            lastNotifiedMilestone = challenge.lastNotifiedMilestone
        )
    }
}
