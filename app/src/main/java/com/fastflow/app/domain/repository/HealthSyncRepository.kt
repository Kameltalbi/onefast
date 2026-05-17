package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.HealthSyncSnapshot
import kotlinx.coroutines.flow.Flow

interface HealthSyncRepository {
    fun observeWriteWeightEnabled(): Flow<Boolean>
    suspend fun setWriteWeightEnabled(enabled: Boolean)
    suspend fun getRequiredPermissions(): Set<String>
    suspend fun getGrantedPermissions(): Set<String>
    suspend fun syncHealthData(): Result<HealthSyncSnapshot>
    suspend fun writeWeightToHealth(weightKg: Float): Result<Unit>
    suspend fun getLastSnapshot(): HealthSyncSnapshot?
}
