package com.fastflow.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    preferencesManager: PreferencesManager
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
    }
}

data class ProfileUiState(
    val defaultPlan: FastingType? = null
)
