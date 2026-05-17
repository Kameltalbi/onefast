package com.fastflow.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.notification.AlarmScheduler
import com.fastflow.app.data.notification.NotificationHelper
import com.fastflow.app.data.notification.SmartNotificationScheduler
import com.fastflow.app.data.notification.StreakMilestoneNotifier
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.UserStats
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.repository.StatsRepository
import com.fastflow.app.domain.usecase.fasting.*
import com.fastflow.app.domain.usecase.stats.GetUserStatsUseCase
import com.fastflow.app.presentation.localization.MotivationMessageProvider
import com.fastflow.app.widget.WidgetStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val startFastingUseCase: StartFastingUseCase,
    private val pauseFastingUseCase: PauseFastingUseCase,
    private val resumeFastingUseCase: ResumeFastingUseCase,
    private val stopFastingUseCase: StopFastingUseCase,
    getCurrentSessionUseCase: GetCurrentSessionUseCase,
    getUserStatsUseCase: GetUserStatsUseCase,
    private val preferencesManager: PreferencesManager,
    private val fastingRepository: FastingRepository,
    private val statsRepository: StatsRepository,
    private val alarmScheduler: AlarmScheduler,
    private val smartNotificationScheduler: SmartNotificationScheduler,
    private val streakMilestoneNotifier: StreakMilestoneNotifier,
    private val notificationHelper: NotificationHelper,
    private val widgetStateManager: WidgetStateManager,
    private val motivationMessageProvider: MotivationMessageProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val tickTrigger = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            getCurrentSessionUseCase().collect { session ->
                _uiState.update { state ->
                    state.copy(
                        currentSession = session,
                        phaseMessage = motivationMessageProvider.getMessage(session, state.userStats)
                    )
                }
                widgetStateManager.update(session)
            }
        }

        viewModelScope.launch {
            getUserStatsUseCase().collect { stats ->
                _uiState.update { state ->
                    state.copy(
                        userStats = stats,
                        phaseMessage = motivationMessageProvider.getMessage(state.currentSession, stats)
                    )
                }
            }
        }

        viewModelScope.launch {
            preferencesManager.defaultFastingType.collect { typeName ->
                val type = typeName?.let { runCatching { FastingType.valueOf(it) }.getOrNull() }
                _uiState.update { it.copy(defaultPlan = type) }
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(1000)
                tickTrigger.value = System.currentTimeMillis()
            }
        }

        viewModelScope.launch {
            tickTrigger.collect { tick ->
                val session = _uiState.value.currentSession
                if (session != null && session.isActive()) {
                    _uiState.update { state ->
                        state.copy(
                            tick = tick,
                            phaseMessage = motivationMessageProvider.getMessage(session, state.userStats)
                        )
                    }
                    widgetStateManager.update(session)
                }
            }
        }
    }

    fun startFasting(fastingType: FastingType, customFastingHours: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (fastingType == FastingType.CUSTOM && customFastingHours != null) {
                preferencesManager.setCustomFastingHours(customFastingHours)
            }
            preferencesManager.setDefaultFastingType(fastingType.name)

            startFastingUseCase(fastingType, customFastingHours)
                .onSuccess { session ->
                    onSessionStarted(session)
                    _uiState.update { it.copy(isLoading = false, error = null, defaultPlan = fastingType) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun onSessionStarted(session: FastingSession) {
        viewModelScope.launch {
            smartNotificationScheduler.scheduleForSession(session)
        }
        notificationHelper.showFastingStartedNotification()
        widgetStateManager.update(session)
    }

    fun pauseFasting() {
        viewModelScope.launch {
            val sessionId = _uiState.value.currentSession?.id ?: return@launch
            pauseFastingUseCase(sessionId)
                .onSuccess {
                    alarmScheduler.cancelAllAlarms()
                    widgetStateManager.update(_uiState.value.currentSession)
                }
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun resumeFasting() {
        viewModelScope.launch {
            val sessionId = _uiState.value.currentSession?.id ?: return@launch
            resumeFastingUseCase(sessionId)
                .onSuccess { session ->
                    smartNotificationScheduler.scheduleForSession(session)
                    widgetStateManager.update(session)
                }
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun stopFasting() {
        viewModelScope.launch {
            val sessionId = _uiState.value.currentSession?.id ?: return@launch
            stopFastingUseCase(sessionId)
                .onSuccess {
                    alarmScheduler.cancelAllAlarms()
                    notificationHelper.showFastingCompletedNotification()
                    val streak = statsRepository.getUserStats().currentStreak
                    streakMilestoneNotifier.notifyIfMilestoneReached(streak)
                    widgetStateManager.update(null)
                }
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class DashboardUiState(
    val currentSession: FastingSession? = null,
    val userStats: UserStats = UserStats(),
    val phaseMessage: String = "",
    val defaultPlan: FastingType? = null,
    val tick: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null
)
