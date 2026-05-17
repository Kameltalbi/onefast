package com.fastflow.app.data.repository

import com.fastflow.app.data.coach.OpenAiCoachService
import com.fastflow.app.data.local.dao.MealPlanDao
import com.fastflow.app.data.local.entity.MealPlanEntity
import com.fastflow.app.data.meal.LocalMealPlanGenerator
import com.fastflow.app.data.meal.MealPlanParser
import com.fastflow.app.data.meal.MealPlanPromptBuilder
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.meal.MealPlan
import com.fastflow.app.domain.model.meal.MealPlanRequest
import com.fastflow.app.domain.repository.MealPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealPlanRepositoryImpl @Inject constructor(
    private val mealPlanDao: MealPlanDao,
    private val openAiCoachService: OpenAiCoachService,
    private val promptBuilder: MealPlanPromptBuilder,
    private val localGenerator: LocalMealPlanGenerator,
    private val parser: MealPlanParser,
    private val preferencesManager: PreferencesManager
) : MealPlanRepository {

    override fun observeHistory(): Flow<List<MealPlan>> =
        mealPlanDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<MealPlan>> =
        mealPlanDao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun isPremiumUser(): Boolean =
        preferencesManager.isPremiumUserOnce()

    override suspend fun generateMealPlan(
        request: MealPlanRequest,
        forceLocal: Boolean
    ): Result<MealPlan> {
        val premium = isPremiumUser()
        val useAi = !forceLocal && premium && openAiCoachService.isConfigured()

        val (planContent, shoppingList, title) = if (useAi) {
            generateWithAi(request).getOrElse { error ->
                if (forceLocal) {
                    val (plan, shopping) = localGenerator.generate(request)
                    Triple(plan, shopping, parser.extractTitle(plan, "Plan ${request.cuisine}"))
                } else {
                    return Result.failure(error)
                }
            }
        } else {
            val (plan, shopping) = localGenerator.generate(request)
            val extractedTitle = parser.extractTitle(plan, "Plan ${request.cuisine}")
            Triple(plan, shopping, extractedTitle)
        }

        val entity = MealPlanEntity(
            title = title,
            caloriesTarget = request.caloriesTarget,
            goal = request.goal.name,
            cuisine = request.cuisine,
            budget = request.budget.name,
            diet = request.diet.name,
            days = request.days,
            planContent = planContent,
            shoppingList = shoppingList
        )
        val id = mealPlanDao.insert(entity)
        val saved = mealPlanDao.getById(id.toInt())!!
        return Result.success(saved.toDomain())
    }

    private suspend fun generateWithAi(request: MealPlanRequest): Result<Triple<String, String, String>> {
        val messages = listOf(
            "system" to promptBuilder.buildSystemPrompt(),
            "user" to promptBuilder.buildUserPrompt(request)
        )
        return openAiCoachService.ask(messages, maxTokens = 1200).map { content ->
            val fallbackTitle = "Plan IA · ${request.cuisine}"
            val title = parser.extractTitle(content, fallbackTitle)
            val shopping = parser.extractShoppingList(
                content,
                localGenerator.generate(request).second
            )
            Triple(content, shopping, title)
        }
    }

    override suspend fun toggleFavorite(planId: Int): Result<Unit> {
        return try {
            val entity = mealPlanDao.getById(planId)
                ?: return Result.failure(Exception("Plan introuvable"))
            mealPlanDao.update(entity.copy(isFavorite = !entity.isFavorite))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePlan(planId: Int): Result<Unit> {
        return try {
            val entity = mealPlanDao.getById(planId)
                ?: return Result.failure(Exception("Plan introuvable"))
            mealPlanDao.delete(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
