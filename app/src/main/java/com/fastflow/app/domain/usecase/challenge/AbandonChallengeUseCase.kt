package com.fastflow.app.domain.usecase.challenge

import com.fastflow.app.domain.repository.ChallengeRepository
import javax.inject.Inject

class AbandonChallengeUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    suspend operator fun invoke(challengeId: Int): Result<Unit> =
        repository.abandonChallenge(challengeId)
}
