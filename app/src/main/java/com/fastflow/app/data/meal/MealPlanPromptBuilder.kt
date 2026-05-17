package com.fastflow.app.data.meal

import com.fastflow.app.domain.model.meal.MealPlanRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealPlanPromptBuilder @Inject constructor() {

    fun buildSystemPrompt(): String =
        "Tu es un nutritionniste expert en jeûne intermittent pour l'app OneFast. " +
            "Réponds en français. Propose des repas adaptés à la fenêtre alimentaire post-jeûne. " +
            "Structure obligatoire :\n" +
            "1) Un titre court\n" +
            "2) Pour chaque jour : Petit-déjeuner / Déjeuner / Dîner avec ingrédients et calories approximatives\n" +
            "3) Section finale « Liste de courses » avec puces (- item)"

    fun buildUserPrompt(request: MealPlanRequest): String {
        return buildString {
            appendLine("Génère un plan repas sur ${request.days} jour(s).")
            appendLine("- Calories cibles : ${request.caloriesTarget} kcal/jour")
            appendLine("- Objectif : ${request.goal.name}")
            appendLine("- Cuisine / pays : ${request.cuisine}")
            appendLine("- Budget : ${request.budget.name}")
            appendLine("- Régime : ${request.diet.name}")
            appendLine("Respecte le régime alimentaire. Privilégie des recettes simples et accessibles.")
        }
    }
}
