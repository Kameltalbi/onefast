package com.fastflow.app.domain.usecase.weight

import com.fastflow.app.domain.model.WeightEntry
import com.fastflow.app.domain.repository.WeightRepository
import javax.inject.Inject

class AddWeightEntryUseCase @Inject constructor(
    private val repository: WeightRepository
) {
    suspend operator fun invoke(
        weight: Float,
        waistCm: Float? = null,
        timestamp: Long = System.currentTimeMillis()
    ): Result<WeightEntry> {
        if (weight <= 0) {
            return Result.failure(IllegalArgumentException("Le poids doit être supérieur à 0"))
        }
        waistCm?.let {
            if (it <= 0) {
                return Result.failure(IllegalArgumentException("Le tour de taille doit être supérieur à 0"))
            }
        }
        return repository.addWeightEntry(weight, timestamp, waistCm)
    }
}
