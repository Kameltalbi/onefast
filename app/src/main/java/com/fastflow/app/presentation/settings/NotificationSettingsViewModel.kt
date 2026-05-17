package com.fastflow.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.notification.SmartNotificationScheduler
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.NotificationPreferences
import com.fastflow.app.domain.repository.FastingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val smartNotificationScheduler: SmartNotificationScheduler,
    private val fastingRepository: FastingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.notificationPreferences.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }

    fun updatePreferences(update: NotificationPreferences) {
        viewModelScope.launch {
            preferencesManager.updateNotificationPreferences(update)
            fastingRepository.getCurrentSession()?.let { session ->
                if (session.isActive()) {
                    smartNotificationScheduler.scheduleForSession(session)
                }
            }
        }
    }
}

data class NotificationSettingsUiState(
    val preferences: NotificationPreferences = NotificationPreferences()
)
