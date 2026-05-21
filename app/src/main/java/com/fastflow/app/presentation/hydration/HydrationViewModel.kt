package com.fastflow.app.presentation.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.DailyHydration
import com.fastflow.app.domain.repository.HydrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val hydrationRepository: HydrationRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.hydrationGoalMl,
                preferencesManager.hydrationGlassMl,
                preferencesManager.hydrationRemindersEnabled
            ) { goal, glass, reminders ->
                Triple(goal, glass, reminders)
            }.flatMapLatest { (goal, glass, reminders) ->
                _uiState.update {
                    it.copy(goalMl = goal, glassSizeMl = glass, remindersEnabled = reminders)
                }
                hydrationRepository.observeTodayHydration(goal, glass)
            }.collect { daily ->
                _uiState.update { it.copy(daily = daily) }
            }
        }
    }

    fun addGlass() {
        addWater(_uiState.value.glassSizeMl)
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            hydrationRepository.addWater(amountMl)
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun removeEntry(entryId: Int) {
        viewModelScope.launch {
            hydrationRepository.removeEntry(entryId)
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun setGoalMl(ml: Int) {
        viewModelScope.launch {
            preferencesManager.setHydrationGoalMl(ml)
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setHydrationRemindersEnabled(enabled)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HydrationUiState(
    val daily: DailyHydration = DailyHydration(0, 2000, 250, emptyList()),
    val goalMl: Int = 2000,
    val glassSizeMl: Int = 250,
    val remindersEnabled: Boolean = true,
    val error: String? = null
)
