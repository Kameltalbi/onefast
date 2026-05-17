package com.fastflow.app.data.repository

import com.fastflow.app.data.health.HealthConnectManager
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.HealthSyncSnapshot
import com.fastflow.app.domain.repository.HealthSyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthSyncRepositoryImpl @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val preferencesManager: PreferencesManager
) : HealthSyncRepository {

    @Volatile
    private var cachedSnapshot: HealthSyncSnapshot? = null

    override fun observeWriteWeightEnabled(): Flow<Boolean> =
        preferencesManager.healthWriteWeightEnabled

    override suspend fun setWriteWeightEnabled(enabled: Boolean) {
        preferencesManager.setHealthWriteWeightEnabled(enabled)
    }

    override suspend fun getRequiredPermissions(): Set<String> =
        healthConnectManager.permissions

    override suspend fun getGrantedPermissions(): Set<String> =
        healthConnectManager.getGrantedPermissions()

    override suspend fun syncHealthData(): Result<HealthSyncSnapshot> {
        return try {
            val lastSync = preferencesManager.getLastHealthSyncAtOnce()
            val snapshot = healthConnectManager.readSnapshot(lastSync)
            cachedSnapshot = snapshot
            if (snapshot.hasPermissions) {
                preferencesManager.setLastHealthSyncAt(snapshot.lastSyncAt ?: System.currentTimeMillis())
            }
            Result.success(snapshot)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLastSnapshot(): HealthSyncSnapshot? {
        cachedSnapshot?.let { return it }
        return HealthSyncSnapshot(
            status = healthConnectManager.getConnectStatus(),
            hasPermissions = healthConnectManager.hasAllPermissions(
                healthConnectManager.getGrantedPermissions()
            ),
            lastSyncAt = preferencesManager.getLastHealthSyncAtOnce(),
            writeWeightEnabled = preferencesManager.getHealthWriteWeightEnabledOnce()
        )
    }

    override suspend fun writeWeightToHealth(weightKg: Float): Result<Unit> {
        if (!preferencesManager.healthWriteWeightEnabled.first()) {
            return Result.success(Unit)
        }
        return healthConnectManager.writeWeightKg(weightKg)
    }
}
