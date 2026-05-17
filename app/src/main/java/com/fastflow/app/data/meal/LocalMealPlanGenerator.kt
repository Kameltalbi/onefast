package com.fastflow.app.data.meal

import com.fastflow.app.domain.model.meal.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMealPlanGenerator @Inject constructor() {

    fun generate(request: MealPlanRequest): Pair<String, String> {
        val title = "Plan ${request.cuisine} · ${request.caloriesTarget} kcal"
        val plan = buildString {
            appendLine("# $title")
            appendLine()
            appendLine("Objectif : ${goalLabel(request.goal)} · Régime : ${dietLabel(request.diet)}")
            appendLine()
            repeat(request.days.coerceIn(1, 7)) { day ->
                appendDay(this, day + 1, request)
            }
        }
        val shopping = buildShoppingList(request)
        return plan to shopping
    }

    private fun appendDay(sb: StringBuilder, day: Int, request: MealPlanRequest) {
        val cals = request.caloriesTarget
        val breakfast = (cals * 0.25).toInt()
        val lunch = (cals * 0.40).toInt()
        val dinner = (cals * 0.35).toInt()

        sb.appendLine("## Jour $day")
        sb.appendLine("### Petit-déjeuner (~$breakfast kcal)")
        sb.appendLine(mealBreakfast(request))
        sb.appendLine()
        sb.appendLine("### Déjeuner (~$lunch kcal)")
        sb.appendLine(mealLunch(request))
        sb.appendLine()
        sb.appendLine("### Dîner (~$dinner kcal)")
        sb.appendLine(mealDinner(request))
        sb.appendLine()
    }

    private fun mealBreakfast(request: MealPlanRequest): String = when (request.diet) {
        DietPreference.VEGAN -> "Porridge avoine + fruits + graines de chia"
        DietPreference.VEGETARIAN -> "Œufs brouillés + pain complet + avocat"
        DietPreference.KETO -> "Œufs + bacon halal + fromage"
        else -> "Œufs + pain complet + fromage blanc + fruit"
    }.let { base ->
        if (request.diet == DietPreference.HALAL) "$base (viande halal uniquement si applicable)"
        else base
    }

    private fun mealLunch(request: MealPlanRequest): String {
        val protein = when (request.diet) {
            DietPreference.VEGAN -> "lentilles ou tofu"
            DietPreference.VEGETARIAN -> "pois chiches ou feta"
            DietPreference.KETO -> "poulet ou poisson"
            DietPreference.HALAL -> "poulet halal grillé"
            else -> "poulet ou poisson"
        }
        return "Salade complète : $protein, quinoa ou riz complet, légumes verts, huile d'olive"
    }

    private fun mealDinner(request: MealPlanRequest): String {
        val main = when (request.diet) {
            DietPreference.VEGAN -> "Curry de légumes + riz basmati"
            DietPreference.VEGETARIAN -> "Légumes rôtis + halloumi"
            DietPreference.KETO -> "Saumon + brocoli + beurre"
            else -> "Poisson ou viande maigre + légumes vapeur"
        }
        return "$main · portion modérée pour ${budgetHint(request.budget)}"
    }

    private fun buildShoppingList(request: MealPlanRequest): String = buildString {
        appendLine("Liste de courses")
        appendLine("- Œufs ou tofu")
        appendLine("- Légumes frais (salade, brocoli, tomates)")
        appendLine("- Protéines : poulet/poisson/lentilles selon régime")
        appendLine("- Féculents : riz, quinoa ou pain complet")
        appendLine("- Fruits de saison")
        appendLine("- Huile d'olive, épices")
        if (request.diet == DietPreference.HALAL) appendLine("- Viandes certifiées halal uniquement")
        if (request.budget == BudgetLevel.LOW) appendLine("- Privilégier surgelés et marque distributeur")
    }

    private fun goalLabel(goal: MealGoal) = when (goal) {
        MealGoal.WEIGHT_LOSS -> "Perte de poids"
        MealGoal.MAINTENANCE -> "Maintien"
        MealGoal.MUSCLE_GAIN -> "Prise de muscle"
    }

    private fun dietLabel(diet: DietPreference) = when (diet) {
        DietPreference.STANDARD -> "Standard"
        DietPreference.HALAL -> "Halal"
        DietPreference.VEGETARIAN -> "Végétarien"
        DietPreference.VEGAN -> "Vegan"
        DietPreference.KETO -> "Keto"
    }

    private fun budgetHint(budget: BudgetLevel) = when (budget) {
        BudgetLevel.LOW -> "budget serré"
        BudgetLevel.MEDIUM -> "budget moyen"
        BudgetLevel.HIGH -> "ingrédients qualité"
    }
}
