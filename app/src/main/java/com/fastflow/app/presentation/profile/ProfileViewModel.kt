package com.fastflow.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.FastingExperienceLevel
import com.fastflow.app.domain.model.OnboardingGoal
import com.fastflow.app.domain.repository.StatsRepository
import com.fastflow.app.domain.usecase.weight.AddWeightEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val age: String = "30",
    val heightCm: String = "170",
    val currentWeightKg: String = "",
    val targetWeightKg: String = "65",
    val goal: OnboardingGoal? = null,
    val experience: FastingExperienceLevel? = null,
    val currentStreak: Int = 0,
    val totalFastsCompleted: Int = 0,
    val isSaving: Boolean = false,
    val initialWeightKg: Float? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val statsRepository: StatsRepository,
    private val addWeightEntryUseCase: AddWeightEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<Int>()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                combine(
                    preferencesManager.communityProfile,
                    preferencesManager.userEmail,
                    preferencesManager.userHeightCm
                ) { profile, email, height ->
                    Triple(profile, email, height)
                },
                combine(
                    preferencesManager.targetWeightKg,
                    preferencesManager.onboardingGoal,
                    preferencesManager.fastingExperience
                ) { target, goalRaw, expRaw ->
                    Triple(target, goalRaw, expRaw)
                }
            ) { (profile, email, height), (target, goalRaw, expRaw) ->
                ProfileFormSnapshot(
                    displayName = profile.displayName,
                    email = email.orEmpty(),
                    heightCm = height,
                    targetWeightKg = target,
                    goal = goalRaw?.let { runCatching { OnboardingGoal.valueOf(it) }.getOrNull() },
                    experience = expRaw?.let { runCatching { FastingExperienceLevel.valueOf(it) }.getOrNull() }
                )
            }.collect { snapshot ->
                _uiState.update { current ->
                    current.copy(
                        displayName = snapshot.displayName,
                        email = snapshot.email,
                        heightCm = snapshot.heightCm?.toInt()?.toString()
                            ?: current.heightCm,
                        targetWeightKg = snapshot.targetWeightKg?.let { "%.1f".format(it) }
                            ?: current.targetWeightKg,
                        goal = snapshot.goal ?: current.goal,
                        experience = snapshot.experience ?: current.experience
                    )
                }
            }
        }
        viewModelScope.launch {
            val age = preferencesManager.getUserAgeOnce()
            if (age != null) {
                _uiState.update { it.copy(age = age.toString()) }
            }
        }
        viewModelScope.launch {
            val stats = statsRepository.getUserStats()
            _uiState.update {
                it.copy(
                    currentStreak = stats.currentStreak,
                    totalFastsCompleted = stats.totalFastsCompleted,
                    currentWeightKg = stats.currentWeight?.let { weight -> "%.1f".format(weight) }
                        ?: it.currentWeightKg,
                    initialWeightKg = stats.currentWeight
                )
            }
        }
    }

    fun updateDisplayName(value: String) {
        _uiState.update { it.copy(displayName = value) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updateAge(value: String) {
        _uiState.update { it.copy(age = value.filter { c -> c.isDigit() }.take(2)) }
    }

    fun updateHeight(value: String) {
        _uiState.update { it.copy(heightCm = value.filter { c -> c.isDigit() }.take(3)) }
    }

    fun updateCurrentWeight(value: String) {
        val filtered = value.filter { c -> c.isDigit() || c == '.' }.take(6)
        _uiState.update { it.copy(currentWeightKg = filtered) }
    }

    fun updateTargetWeight(value: String) {
        val filtered = value.filter { c -> c.isDigit() || c == '.' }.take(6)
        _uiState.update { it.copy(targetWeightKg = filtered) }
    }

    fun selectGoal(goal: OnboardingGoal) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun selectExperience(level: FastingExperienceLevel) {
        _uiState.update { it.copy(experience = level) }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (state.isSaving) return

        val age = state.age.toIntOrNull()
        val height = state.heightCm.toIntOrNull()
        val currentWeight = state.currentWeightKg.toFloatOrNull()
        val targetWeight = state.targetWeightKg.toFloatOrNull()
        val goal = state.goal
        val experience = state.experience

        if (state.displayName.isBlank()) return
        if (age == null || age !in 16..99) return
        if (height == null || height !in 100..250) return
        if (currentWeight == null || currentWeight !in 40f..250f) return
        if (targetWeight == null || targetWeight !in 40f..250f) return
        if (goal == null || experience == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                val profile = preferencesManager.getCommunityProfileOnce()
                preferencesManager.updateCommunityProfile(
                    displayName = state.displayName.trim(),
                    shareAnonymously = profile.shareAnonymously
                )
                preferencesManager.setUserEmail(state.email)
                preferencesManager.setUserAge(age)
                preferencesManager.setUserHeightCm(height.toFloat())
                preferencesManager.setTargetWeightKg(targetWeight)
                preferencesManager.setOnboardingGoal(goal.name)
                preferencesManager.setFastingExperience(experience.name)

                val previousWeight = state.initialWeightKg
                if (previousWeight == null || abs(previousWeight - currentWeight) >= 0.05f) {
                    addWeightEntryUseCase(currentWeight, null).getOrThrow()
                    _uiState.update { it.copy(initialWeightKg = currentWeight) }
                }
            }.onSuccess {
                _snackbarMessage.emit(com.fastflow.app.R.string.profile_saved)
            }.onFailure {
                _snackbarMessage.emit(com.fastflow.app.R.string.profile_save_error)
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    private data class ProfileFormSnapshot(
        val displayName: String,
        val email: String,
        val heightCm: Float?,
        val targetWeightKg: Float?,
        val goal: OnboardingGoal?,
        val experience: FastingExperienceLevel?
    )
}
