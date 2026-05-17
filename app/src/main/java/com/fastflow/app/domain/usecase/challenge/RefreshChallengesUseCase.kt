package com.fastflow.app.domain.usecase.challenge

import com.fastflow.app.domain.repository.ChallengeRepository
import javax.inject.Inject

class RefreshChallengesUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    suspend operator fun invoke() = repository.refreshActiveChallenges()
}
