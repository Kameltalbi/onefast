package com.fastflow.app.presentation.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.ChatMessage
import com.fastflow.app.domain.model.CoachQuota
import com.fastflow.app.domain.repository.CoachRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val coachRepository: CoachRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            coachRepository.observeMessages().collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
        refreshQuota()
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            coachRepository.sendMessage(text)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, inputText = "") }
                    refreshQuota()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun useSuggestion(suggestion: String) {
        sendMessage(suggestion)
    }

    fun clearHistory() {
        viewModelScope.launch {
            coachRepository.clearHistory()
        }
    }

    private fun refreshQuota() {
        viewModelScope.launch {
            val remaining = coachRepository.getRemainingFreeQuestions()
            val premium = coachRepository.isPremiumUser()
            _uiState.update {
                it.copy(
                    remainingQuestions = remaining,
                    isPremium = premium,
                    dailyLimit = CoachQuota.FREE_DAILY_LIMIT
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class CoachUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val remainingQuestions: Int = CoachQuota.FREE_DAILY_LIMIT,
    val dailyLimit: Int = CoachQuota.FREE_DAILY_LIMIT,
    val isPremium: Boolean = false
)
