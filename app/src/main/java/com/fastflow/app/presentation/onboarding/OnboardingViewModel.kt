package com.fastflow.app.presentation.onboarding

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.core.locale.AppLocaleManager
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingExperienceLevel
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.OnboardingGoal
import com.fastflow.app.domain.onboarding.FastingPlanRecommender
import com.fastflow.app.domain.usecase.weight.AddWeightEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val step: Int = 0,
    val languageTag: String = AppLocaleManager.LANGUAGE_FRENCH,
    val goal: OnboardingGoal? = null,
    val age: Int = 30,
    val heightCm: Int = 170,
    val weightKg: Float = 70f,
    val targetWeightKg: Float = 65f,
    val experience: FastingExperienceLevel? = null,
    val recommendedPlan: FastingType = FastingType.SIXTEEN_EIGHT,
    val isSaving: Boolean = false
) {
    val progressStep: Int get() = step + 1

    fun canAdvanceFromGoal(): Boolean = goal != null

    companion object {
        const val TOTAL_STEPS: Int = 5
    }

    fun canAdvanceFromProfile(): Boolean = age in 16..99 && heightCm in 100..250 &&
        weightKg in 40f..250f && targetWeightKg in 40f..250f

    fun canAdvanceFromLevel(): Boolean = experience != null
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val addWeightEntryUseCase: AddWeightEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = preferencesManager.getAppLanguageOnce()
            val tag = saved ?: AppLocaleManager.defaultLanguageTag()
            _uiState.update { it.copy(languageTag = tag) }
            if (saved == null) {
                preferencesManager.setAppLanguage(tag)
                AppLocaleManager.apply(tag)
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

    fun nextStep() {
        _uiState.update { state ->
            val next = (state.step + 1).coerceAtMost(OnboardingUiState.TOTAL_STEPS - 1)
            if (next == OnboardingUiState.TOTAL_STEPS - 1) {
                state.copy(
                    step = next,
                    recommendedPlan = FastingPlanRecommender.recommend(state.goal, state.experience)
                )
            } else {
                state.copy(step = next)
            }
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            state.copy(step = (state.step - 1).coerceAtLeast(0))
        }
    }

    fun selectGoal(goal: OnboardingGoal) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun selectExperience(level: FastingExperienceLevel) {
        _uiState.update { it.copy(experience = level) }
    }

    fun updateAge(age: Int) {
        _uiState.update { it.copy(age = age) }
    }

    fun updateHeight(heightCm: Int) {
        _uiState.update { it.copy(heightCm = heightCm) }
    }

    fun updateWeight(weightKg: Float) {
        _uiState.update { it.copy(weightKg = weightKg) }
    }

    fun updateTargetWeight(targetKg: Float) {
        _uiState.update { it.copy(targetWeightKg = targetKg) }
    }

    fun completeOnboarding(onDone: () -> Unit) {
        val state = _uiState.value
        if (state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val plan = FastingPlanRecommender.recommend(state.goal, state.experience)
            preferencesManager.setAppLanguage(state.languageTag)
            AppLocaleManager.apply(state.languageTag)
            preferencesManager.setDefaultFastingType(plan.name)
            preferencesManager.setUserAge(state.age)
            preferencesManager.setUserHeightCm(state.heightCm.toFloat())
            preferencesManager.setTargetWeightKg(state.targetWeightKg)
            state.goal?.let { preferencesManager.setOnboardingGoal(it.name) }
            state.experience?.let { preferencesManager.setFastingExperience(it.name) }
            addWeightEntryUseCase(state.weightKg, null)
            preferencesManager.setOnboardingCompleted(true)
            _uiState.update { it.copy(isSaving = false) }
            onDone()
        }
    }
}
