package com.fastflow.app.domain.model

data class ChallengeDefinition(
    val type: ChallengeType,
    val durationDays: Int,
    val minFastingHours: Float,
    val badgeId: String
)

data class UserChallenge(
    val id: Int,
    val type: ChallengeType,
    val startedAt: Long,
    val status: ChallengeStatus,
    val completedDays: Int,
    val lastNotifiedMilestone: Int
) {
    fun progressPercent(definition: ChallengeDefinition): Int =
        if (definition.durationDays <= 0) 0
        else ((completedDays.toFloat() / definition.durationDays) * 100).toInt().coerceIn(0, 100)

    fun isActive(): Boolean = status == ChallengeStatus.ACTIVE
}

data class EarnedBadge(
    val id: String,
    val challengeType: ChallengeType,
    val earnedAt: Long
)

data class ChallengeUiModel(
    val definition: ChallengeDefinition,
    val enrollment: UserChallenge?,
    val canJoin: Boolean
)
