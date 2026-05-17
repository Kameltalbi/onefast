package com.fastflow.app.domain.usecase.fasting

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.repository.FastingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentSessionUseCase @Inject constructor(
    private val repository: FastingRepository
) {
    operator fun invoke(): Flow<FastingSession?> {
        return repository.observeCurrentSession()
    }
}
