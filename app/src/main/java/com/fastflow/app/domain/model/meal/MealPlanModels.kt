package com.fastflow.app.domain.model.meal

enum class MealGoal(val labelKey: String) {
    WEIGHT_LOSS("meal_goal_loss"),
    MAINTENANCE("meal_goal_maintain"),
    MUSCLE_GAIN("meal_goal_muscle")
}

enum class DietPreference(val labelKey: String) {
    STANDARD("meal_diet_standard"),
    HALAL("meal_diet_halal"),
    VEGETARIAN("meal_diet_vegetarian"),
    VEGAN("meal_diet_vegan"),
    KETO("meal_diet_keto")
}

enum class BudgetLevel(val labelKey: String) {
    LOW("meal_budget_low"),
    MEDIUM("meal_budget_medium"),
    HIGH("meal_budget_high")
}

data class MealPlanRequest(
    val caloriesTarget: Int,
    val goal: MealGoal,
    val cuisine: String,
    val budget: BudgetLevel,
    val diet: DietPreference,
    val days: Int = 3
)

data class MealPlan(
    val id: Int = 0,
    val title: String,
    val request: MealPlanRequest,
    val planContent: String,
    val shoppingList: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
