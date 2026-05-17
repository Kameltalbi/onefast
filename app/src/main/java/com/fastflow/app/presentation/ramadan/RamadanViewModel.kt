package com.fastflow.app.presentation.ramadan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.ramadan.RamadanNextEvent
import com.fastflow.app.domain.model.ramadan.RamadanSettings
import com.fastflow.app.domain.model.ramadan.RamadanTimings
import com.fastflow.app.domain.repository.RamadanRepository
import com.fastflow.app.domain.usecase.fasting.StartFastingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RamadanViewModel @Inject constructor(
    private val ramadanRepository: RamadanRepository,
    private val startFastingUseCase: StartFastingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RamadanUiState())
    val uiState: StateFlow<RamadanUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ramadanRepository.observeSettings().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
        viewModelScope.launch {
            val cached = ramadanRepository.getCachedTimings()
            _uiState.update { it.copy(timings = cached, isLoading = false) }
            if (_uiState.value.settings.enabled && cached == null) {
                refreshTimings()
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(tick = System.currentTimeMillis()) }
            }
        }
    }

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = _uiState.value.settings
            ramadanRepository.updateSettings(current.copy(enabled = enabled))
            if (enabled) refreshTimings()
        }
    }

    fun updateLocation(city: String, country: String) {
        _uiState.update {
            it.copy(
                settings = it.settings.copy(
                    city = city,
                    country = country,
                    latitude = null,
                    longitude = null
                )
            )
        }
    }

    fun setHydrationReminders(enabled: Boolean) {
        viewModelScope.launch {
            val settings = _uiState.value.settings.copy(hydrationRemindersEnabled = enabled)
            ramadanRepository.updateSettings(settings)
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            ramadanRepository.updateSettings(_uiState.value.settings)
            refreshTimings()
        }
    }

    fun refreshTimings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            ramadanRepository.refreshTimings()
                .onSuccess { timings ->
                    _uiState.update { it.copy(timings = timings, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun startRamadanFast() {
        val timings = _uiState.value.timings ?: return
        val hours = timings.fastingHoursUntilIftar().toInt().coerceIn(1, 23)
        viewModelScope.launch {
            startFastingUseCase(FastingType.CUSTOM, hours)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class RamadanUiState(
    val settings: RamadanSettings = RamadanSettings(),
    val timings: RamadanTimings? = null,
    val tick: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun nextEvent(now: Long = tick): RamadanNextEvent = timings?.let { t ->
        when {
            now < t.fajrMillis -> RamadanNextEvent.SUHOOR
            now < t.maghribMillis -> RamadanNextEvent.IFTAR
            else -> RamadanNextEvent.SUHOOR
        }
    } ?: RamadanNextEvent.IFTAR

    fun millisUntilNextEvent(now: Long = tick): Long = timings?.let { t ->
        when (nextEvent(now)) {
            RamadanNextEvent.IFTAR -> t.millisUntilMaghrib(now)
            RamadanNextEvent.SUHOOR -> t.millisUntilFajr(now)
        }
    } ?: 0L

    val isFasting: Boolean
        get() = timings?.isCurrentlyFasting(tick) == true
}
