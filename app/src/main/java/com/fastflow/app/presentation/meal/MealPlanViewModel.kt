package com.fastflow.app.presentation.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.meal.*
import com.fastflow.app.domain.repository.MealPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isPremium = mealPlanRepository.isPremiumUser()) }
        }
        viewModelScope.launch {
            mealPlanRepository.observeHistory().collect { history ->
                _uiState.update { it.copy(history = history) }
            }
        }
        viewModelScope.launch {
            mealPlanRepository.observeFavorites().collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
    }

    fun updateCalories(value: String) {
        _uiState.update { it.copy(caloriesText = value) }
    }

    fun updateCuisine(value: String) {
        _uiState.update { it.copy(cuisine = value) }
    }

    fun setGoal(goal: MealGoal) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun setDiet(diet: DietPreference) {
        _uiState.update { it.copy(diet = diet) }
    }

    fun setBudget(budget: BudgetLevel) {
        _uiState.update { it.copy(budget = budget) }
    }

    fun setDays(days: Int) {
        _uiState.update { it.copy(days = days.coerceIn(1, 7)) }
    }

    fun selectPlan(plan: MealPlan?) {
        _uiState.update { it.copy(selectedPlan = plan) }
    }

    fun generate(forceLocal: Boolean = false) {
        val state = _uiState.value
        val calories = state.caloriesText.toIntOrNull()
        if (calories == null || calories !in 1200..4000) {
            _uiState.update { it.copy(error = "Calories entre 1200 et 4000") }
            return
        }
        if (state.cuisine.trim().length < 2) {
            _uiState.update { it.copy(error = "Indiquez une cuisine ou un pays") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            val request = MealPlanRequest(
                caloriesTarget = calories,
                goal = state.goal,
                cuisine = state.cuisine.trim(),
                budget = state.budget,
                diet = state.diet,
                days = state.days
            )
            mealPlanRepository.generateMealPlan(request, forceLocal)
                .onSuccess { plan ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            selectedPlan = plan,
                            selectedTab = 1
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isGenerating = false, error = e.message)
                    }
                }
        }
    }

    fun toggleFavorite(planId: Int) {
        viewModelScope.launch {
            mealPlanRepository.toggleFavorite(planId)
        }
    }

    fun deletePlan(planId: Int) {
        viewModelScope.launch {
            mealPlanRepository.deletePlan(planId)
            _uiState.update {
                if (it.selectedPlan?.id == planId) it.copy(selectedPlan = null) else it
            }
        }
    }

    fun setTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class MealPlanUiState(
    val caloriesText: String = "1800",
    val goal: MealGoal = MealGoal.WEIGHT_LOSS,
    val cuisine: String = "Maroc / Méditerranéen",
    val budget: BudgetLevel = BudgetLevel.MEDIUM,
    val diet: DietPreference = DietPreference.HALAL,
    val days: Int = 3,
    val isPremium: Boolean = false,
    val isGenerating: Boolean = false,
    val selectedPlan: MealPlan? = null,
    val history: List<MealPlan> = emptyList(),
    val favorites: List<MealPlan> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null
)
