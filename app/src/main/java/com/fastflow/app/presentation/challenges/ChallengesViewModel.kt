package com.fastflow.app.presentation.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.domain.model.ChallengeUiModel
import com.fastflow.app.domain.model.EarnedBadge
import com.fastflow.app.domain.repository.ChallengeRepository
import com.fastflow.app.domain.usecase.challenge.AbandonChallengeUseCase
import com.fastflow.app.domain.usecase.challenge.JoinChallengeUseCase
import com.fastflow.app.domain.usecase.challenge.RefreshChallengesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val joinChallengeUseCase: JoinChallengeUseCase,
    private val abandonChallengeUseCase: AbandonChallengeUseCase,
    private val refreshChallengesUseCase: RefreshChallengesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { refreshChallengesUseCase() }

        viewModelScope.launch {
            combine(
                challengeRepository.observeChallengesOverview(),
                challengeRepository.observeEarnedBadges()
            ) { challenges, badges ->
                ChallengesUiState(
                    challenges = challenges,
                    badges = badges,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state.copy(
                    error = _uiState.value.error,
                    message = _uiState.value.message
                )
            }
        }
    }

    fun joinChallenge(type: ChallengeType) {
        viewModelScope.launch {
            joinChallengeUseCase(type)
                .onSuccess {
                    refreshChallengesUseCase()
                    _uiState.update { it.copy(message = null, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun abandonChallenge(challengeId: Int) {
        viewModelScope.launch {
            abandonChallengeUseCase(challengeId)
                .onSuccess { refreshChallengesUseCase() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            refreshChallengesUseCase()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ChallengesUiState(
    val challenges: List<ChallengeUiModel> = emptyList(),
    val badges: List<EarnedBadge> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val message: String? = null
)
