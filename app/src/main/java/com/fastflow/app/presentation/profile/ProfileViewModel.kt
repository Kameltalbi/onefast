package com.fastflow.app.presentation.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.core.locale.AppLocaleManager
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.defaultFastingType.collect { typeName ->
                val type = typeName?.let { runCatching { FastingType.valueOf(it) }.getOrNull() }
                _uiState.update { it.copy(defaultPlan = type) }
            }
        }
        viewModelScope.launch {
            preferencesManager.appLanguage.collect { tag ->
                _uiState.update {
                    it.copy(languageTag = tag ?: AppLocaleManager.defaultLanguageTag())
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
}

data class ProfileUiState(
    val defaultPlan: FastingType? = null,
    val languageTag: String = AppLocaleManager.LANGUAGE_FRENCH
)
