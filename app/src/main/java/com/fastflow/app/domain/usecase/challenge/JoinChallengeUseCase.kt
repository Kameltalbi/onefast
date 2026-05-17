package com.fastflow.app.domain.usecase.challenge

import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.domain.model.UserChallenge
import com.fastflow.app.domain.repository.ChallengeRepository
import javax.inject.Inject

class JoinChallengeUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    suspend operator fun invoke(type: ChallengeType): Result<UserChallenge> =
        repository.joinChallenge(type)
}
