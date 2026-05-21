package com.fastflow.app.presentation.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.core.locale.AppLocaleManager
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val languageTag: String = AppLocaleManager.LANGUAGE_FRENCH,
    val defaultPlan: FastingType? = null,
    val autoStartEnabled: Boolean = false,
    val autoStartHour: Int = 20,
    val autoStartMinute: Int = 0,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.appLanguage.collect { tag ->
                _uiState.update {
                    it.copy(languageTag = tag ?: AppLocaleManager.defaultLanguageTag())
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
            subscriptionRepository.observeTier().collect { tier ->
                _uiState.update { it.copy(subscriptionTier = tier) }
            }
        }
        viewModelScope.launch {
            combine(
                preferencesManager.autoStartEnabled,
                preferencesManager.autoStartHour,
                preferencesManager.autoStartMinute
            ) { enabled, hour, minute ->
                Triple(enabled, hour, minute)
            }.collect { (enabled, hour, minute) ->
                _uiState.update {
                    it.copy(
                        autoStartEnabled = enabled,
                        autoStartHour = hour,
                        autoStartMinute = minute
                    )
                }
            }
        }
    }

    fun selectLanguage(languageTag: String, activity: Activity) {
        if (languageTag == _uiState.value.languageTag) return
        viewModelScope.launch {
            preferencesManager.setAppLanguage(languageTag)
            AppLocaleManager.apply(languageTag)
            _uiState.update { it.copy(languageTag = languageTag) }
            activity.recreate()
        }
    }

    fun setDefaultPlan(type: FastingType, customHours: Int?) {
        viewModelScope.launch {
            preferencesManager.setDefaultFastingType(type.name)
            if (type == FastingType.CUSTOM && customHours != null) {
                preferencesManager.setCustomFastingHours(customHours)
            }
        }
    }

    fun setAutoStartEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoStartEnabled(enabled)
        }
    }

    fun setAutoStartTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesManager.setAutoStartTime(hour, minute)
        }
    }
}
