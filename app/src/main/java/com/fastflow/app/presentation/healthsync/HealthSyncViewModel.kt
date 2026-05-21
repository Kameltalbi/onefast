package com.fastflow.app.presentation.healthsync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.HealthConnectStatus
import com.fastflow.app.domain.model.HealthSyncSnapshot
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.domain.repository.HealthSyncRepository
import com.fastflow.app.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthSyncViewModel @Inject constructor(
    private val healthSyncRepository: HealthSyncRepository,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthSyncUiState())
    val uiState: StateFlow<HealthSyncUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            subscriptionRepository.observeTier().collect { tier ->
                _uiState.update { it.copy(subscriptionTier = tier) }
            }
        }
        viewModelScope.launch {
            healthSyncRepository.observeWriteWeightEnabled().collect { enabled ->
                _uiState.update { it.copy(writeWeightEnabled = enabled) }
            }
        }
        refreshStatus()
    }

    fun refreshStatus() {
        viewModelScope.launch {
            val required = healthSyncRepository.getRequiredPermissions()
            val granted = healthSyncRepository.getGrantedPermissions()
            val snapshot = healthSyncRepository.getLastSnapshot()
            _uiState.update {
                it.copy(
                    snapshot = snapshot,
                    requiredPermissions = required,
                    grantedPermissions = granted,
                    isLoading = false
                )
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            healthSyncRepository.syncHealthData()
                .onSuccess { snapshot ->
                    _uiState.update { it.copy(snapshot = snapshot, isSyncing = false) }
                    refreshStatus()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSyncing = false, error = e.message) }
                }
        }
    }

    fun onPermissionsResult(granted: Set<String>) {
        _uiState.update { it.copy(grantedPermissions = granted) }
        if (granted.containsAll(_uiState.value.requiredPermissions)) {
            syncNow()
        }
    }

    fun setWriteWeightEnabled(enabled: Boolean) {
        viewModelScope.launch {
            healthSyncRepository.setWriteWeightEnabled(enabled)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HealthSyncUiState(
    val snapshot: HealthSyncSnapshot? = null,
    val requiredPermissions: Set<String> = emptySet(),
    val grantedPermissions: Set<String> = emptySet(),
    val writeWeightEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE
) {
    val connectStatus: HealthConnectStatus
        get() = snapshot?.status ?: HealthConnectStatus.NOT_SUPPORTED

    val hasAllPermissions: Boolean
        get() = requiredPermissions.isNotEmpty() &&
            grantedPermissions.containsAll(requiredPermissions)
}
