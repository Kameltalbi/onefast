package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.meal.*

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val caloriesTarget: Int,
    val goal: String,
    val cuisine: String,
    val budget: String,
    val diet: String,
    val days: Int,
    val planContent: String,
    val shoppingList: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): MealPlan = MealPlan(
        id = id,
        title = title,
        request = MealPlanRequest(
            caloriesTarget = caloriesTarget,
            goal = MealGoal.valueOf(goal),
            cuisine = cuisine,
            budget = BudgetLevel.valueOf(budget),
            diet = DietPreference.valueOf(diet),
            days = days
        ),
        planContent = planContent,
        shoppingList = shoppingList,
        isFavorite = isFavorite,
        createdAt = createdAt
    )

    companion object {
        fun fromPlan(plan: MealPlan): MealPlanEntity = MealPlanEntity(
            id = plan.id,
            title = plan.title,
            caloriesTarget = plan.request.caloriesTarget,
            goal = plan.request.goal.name,
            cuisine = plan.request.cuisine,
            budget = plan.request.budget.name,
            diet = plan.request.diet.name,
            days = plan.request.days,
            planContent = plan.planContent,
            shoppingList = plan.shoppingList,
            isFavorite = plan.isFavorite,
            createdAt = plan.createdAt
        )
    }
}
