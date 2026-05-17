package com.fastflow.app.domain.usecase.weight

import com.fastflow.app.domain.model.WeightEntry
import com.fastflow.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeightHistoryUseCase @Inject constructor(
    private val repository: WeightRepository
) {
    operator fun invoke(): Flow<List<WeightEntry>> {
        return repository.observeWeightEntries()
    }
}
