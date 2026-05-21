package com.fastflow.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.history.CalendarDayCell
import com.fastflow.app.domain.history.FastingCalendarBuilder
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.UserStats
import com.fastflow.app.domain.usecase.fasting.GetCurrentSessionUseCase
import com.fastflow.app.domain.usecase.fasting.GetFastingHistoryUseCase
import com.fastflow.app.domain.model.SubscriptionCapabilities
import com.fastflow.app.domain.model.SubscriptionFeature
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.domain.repository.SubscriptionRepository
import com.fastflow.app.domain.usecase.stats.GetUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FastingHistoryViewModel @Inject constructor(
    private val getFastingHistoryUseCase: GetFastingHistoryUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    getCurrentSessionUseCase: GetCurrentSessionUseCase,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    companion object {
        private const val FREE_HISTORY_LIMIT = 7
    }

    private val _uiState = MutableStateFlow(FastingHistoryUiState())
    val uiState: StateFlow<FastingHistoryUiState> = _uiState.asStateFlow()

    private var allSessions: List<FastingSession> = emptyList()
    private var activeSession: FastingSession? = null

    init {
        viewModelScope.launch {
            getCurrentSessionUseCase().collect { session ->
                activeSession = session?.takeIf { it.isActive() }
                rebuildCalendar()
            }
        }
        viewModelScope.launch {
            subscriptionRepository.observeTier().collect { tier ->
                _uiState.update { it.copy(subscriptionTier = tier) }
                applySessionList()
            }
        }
        loadHistory()
    }

    private fun applySessionList() {
        val tier = _uiState.value.subscriptionTier
        val hasUnlimited = SubscriptionCapabilities.hasAccess(
            tier,
            SubscriptionFeature.UNLIMITED_HISTORY
        )
        val sessions = if (hasUnlimited) {
            allSessions
        } else {
            allSessions.take(FREE_HISTORY_LIMIT)
        }
        _uiState.update { it.copy(allSessions = sessions, isHistoryLimited = !hasUnlimited) }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            allSessions = getFastingHistoryUseCase()
            val stats = getUserStatsUseCase().first()
            _uiState.update { it.copy(stats = stats, isLoading = false) }
            applySessionList()
            rebuildCalendar()
        }
    }

    fun previousMonth() {
        val calendar = Calendar.getInstance().apply {
            set(_uiState.value.displayYear, _uiState.value.displayMonth, 1)
            add(Calendar.MONTH, -1)
        }
        _uiState.update {
            it.copy(
                displayYear = calendar.get(Calendar.YEAR),
                displayMonth = calendar.get(Calendar.MONTH),
                selectedDayMillis = null
            )
        }
        rebuildCalendar()
    }

    fun nextMonth() {
        val calendar = Calendar.getInstance().apply {
            set(_uiState.value.displayYear, _uiState.value.displayMonth, 1)
            add(Calendar.MONTH, 1)
        }
        _uiState.update {
            it.copy(
                displayYear = calendar.get(Calendar.YEAR),
                displayMonth = calendar.get(Calendar.MONTH),
                selectedDayMillis = null
            )
        }
        rebuildCalendar()
    }

    fun selectDay(dayStartMillis: Long) {
        val current = _uiState.value.selectedDayMillis
        _uiState.update {
            it.copy(
                selectedDayMillis = if (current == dayStartMillis) null else dayStartMillis
            )
        }
    }

    private fun rebuildCalendar() {
        val state = _uiState.value
        val month = FastingCalendarBuilder.buildMonth(
            year = state.displayYear,
            month = state.displayMonth,
            completedSessions = allSessions,
            activeSession = activeSession,
            locale = Locale.getDefault()
        )
        _uiState.update {
            it.copy(calendarCells = month.cells, monthLabel = month.monthLabel)
        }
    }
}

data class FastingHistoryUiState(
    val allSessions: List<FastingSession> = emptyList(),
    val stats: UserStats = UserStats(),
    val calendarCells: List<CalendarDayCell> = emptyList(),
    val monthLabel: String = "",
    val displayYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val displayMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedDayMillis: Long? = null,
    val isLoading: Boolean = false,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val isHistoryLimited: Boolean = false
) {
    val hasAdvancedStats: Boolean
        get() = SubscriptionCapabilities.hasAccess(
            subscriptionTier,
            SubscriptionFeature.ADVANCED_STATS
        )

    val hasCalendar: Boolean
        get() = hasAdvancedStats
    val visibleSessions: List<FastingSession>
        get() {
            val selected = selectedDayMillis ?: return allSessions
            val dayEnd = selected + 24 * 60 * 60 * 1000
            return allSessions.filter { it.startTime in selected until dayEnd }
        }

    val isEmpty: Boolean get() = allSessions.isEmpty() && !isLoading
}
