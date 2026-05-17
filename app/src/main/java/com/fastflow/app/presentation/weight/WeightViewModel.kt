package com.fastflow.app.presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.usecase.weight.AddWeightEntryUseCase
import com.fastflow.app.domain.usecase.weight.GetWeightHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val addWeightEntryUseCase: AddWeightEntryUseCase,
    getWeightHistoryUseCase: GetWeightHistoryUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightUiState())
    val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getWeightHistoryUseCase(),
                preferencesManager.targetWeightKg
            ) { entries, target ->
                entries to target
            }.collect { (entries, target) ->
                _uiState.update {
                    it.copy(
                        weightHistory = entries,
                        currentWeight = entries.firstOrNull()?.getWeightInKg(),
                        targetWeightKg = target
                    )
                }
            }
        }
    }

    fun addWeight(weight: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            addWeightEntryUseCase(weight, null)
                .onSuccess {
                    _uiState.update {
                        it.copy(isLoading = false, error = null, showAddDialog = false)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }

    fun setTargetWeight(weight: Float) {
        viewModelScope.launch {
            preferencesManager.setTargetWeightKg(weight)
            hideGoalDialog()
        }
    }

    fun clearTargetWeight() {
        viewModelScope.launch {
            preferencesManager.setTargetWeightKg(null)
            hideGoalDialog()
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showGoalDialog() {
        _uiState.update { it.copy(showGoalDialog = true) }
    }

    fun hideGoalDialog() {
        _uiState.update { it.copy(showGoalDialog = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class WeightUiState(
    val weightHistory: List<com.fastflow.app.domain.model.WeightEntry> = emptyList(),
    val currentWeight: Float? = null,
    val targetWeightKg: Float? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val showGoalDialog: Boolean = false
)
