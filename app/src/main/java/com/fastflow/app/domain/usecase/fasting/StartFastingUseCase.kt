package com.fastflow.app.domain.usecase.fasting

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.repository.FastingRepository
import javax.inject.Inject

class StartFastingUseCase @Inject constructor(
    private val repository: FastingRepository
) {
    suspend operator fun invoke(
        fastingType: FastingType,
        customFastingHours: Int? = null
    ): Result<FastingSession> {
        return repository.startFasting(fastingType, customFastingHours)
    }
}
