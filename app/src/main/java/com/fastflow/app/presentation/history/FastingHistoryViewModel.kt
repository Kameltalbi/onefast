package com.fastflow.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.usecase.fasting.GetFastingHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FastingHistoryViewModel @Inject constructor(
    private val getFastingHistoryUseCase: GetFastingHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FastingHistoryUiState())
    val uiState: StateFlow<FastingHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val sessions = getFastingHistoryUseCase()
            _uiState.update { it.copy(sessions = sessions, isLoading = false) }
        }
    }
}

data class FastingHistoryUiState(
    val sessions: List<FastingSession> = emptyList(),
    val isLoading: Boolean = false
)
