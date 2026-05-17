package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.domain.model.ChallengeUiModel
import com.fastflow.app.domain.model.EarnedBadge
import com.fastflow.app.domain.model.UserChallenge
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun observeChallengesOverview(): Flow<List<ChallengeUiModel>>
    fun observeEarnedBadges(): Flow<List<EarnedBadge>>
    suspend fun joinChallenge(type: ChallengeType): Result<UserChallenge>
    suspend fun abandonChallenge(challengeId: Int): Result<Unit>
    suspend fun refreshActiveChallenges(): List<UserChallenge>
}
