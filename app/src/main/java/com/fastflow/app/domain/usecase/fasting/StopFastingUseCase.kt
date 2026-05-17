package com.fastflow.app.domain.usecase.fasting

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.repository.FastingRepository
import javax.inject.Inject

class StopFastingUseCase @Inject constructor(
    private val repository: FastingRepository
) {
    suspend operator fun invoke(sessionId: Int): Result<FastingSession> {
        return repository.stopFasting(sessionId)
    }
}
