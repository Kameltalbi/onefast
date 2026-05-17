package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.meal.MealPlan
import com.fastflow.app.domain.model.meal.MealPlanRequest
import kotlinx.coroutines.flow.Flow

interface MealPlanRepository {
    fun observeHistory(): Flow<List<MealPlan>>
    fun observeFavorites(): Flow<List<MealPlan>>
    suspend fun isPremiumUser(): Boolean
    suspend fun generateMealPlan(request: MealPlanRequest, forceLocal: Boolean = false): Result<MealPlan>
    suspend fun toggleFavorite(planId: Int): Result<Unit>
    suspend fun deletePlan(planId: Int): Result<Unit>
}
