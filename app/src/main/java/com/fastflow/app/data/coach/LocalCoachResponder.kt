package com.fastflow.app.data.coach

import com.fastflow.app.domain.model.CoachContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalCoachResponder @Inject constructor() {

    fun respond(userMessage: String, context: CoachContext): String {
        val msg = userMessage.lowercase()

        return when {
            msg.contains("faim") || msg.contains("affam") ->
                "La faim pendant un jeûne est normale, surtout les premières heures. " +
                    "Buvez un grand verre d'eau ou un thé/café sans sucre. " +
                    "Vous êtes en ${context.fastingStatus.lowercase()} — tenez encore un peu, votre corps s'adapte. " +
                    "Si la faim est intense ou vous vous sentez mal, écoutez votre corps."

            msg.contains("café") || msg.contains("cafe") || msg.contains("thé") || msg.contains("the ") ->
                "Oui, café ou thé sans sucre ni lait pendant le jeûne est généralement accepté et peut aider à la faim. " +
                    "Évitez les boissons sucrées ou les jus. Restez bien hydraté."

            msg.contains("fatigu") || msg.contains("énergie") || msg.contains("energie") ->
                "La fatigue peut apparaître en début de jeûne. Priorisez le sommeil, l'eau et une activité légère. " +
                    "Si la fatigue persiste plusieurs jours, réduisez la durée du jeûne ou consultez un professionnel."

            msg.contains("manger") || msg.contains("repas") || msg.contains("rompre") || msg.contains("casser") ->
                "Pour rompre le jeûne : commencez par de l'eau, puis un repas équilibré (protéines + légumes + bonnes graisses). " +
                    "Évitez le sucre en excès pour limiter le pic glycémique. Mangez lentement sur 20-30 minutes."

            msg.contains("eau") || msg.contains("hydrat") || msg.contains("boire") ->
                "L'hydratation est essentielle pendant le jeûne. Visez 2 à 3 litres par jour. " +
                    "Eau, thé, café sans sucre — tout compte."

            msg.contains("perdre") || msg.contains("poids") ->
                "Le jeûne intermittent aide la perte de poids en créant un déficit calorique. " +
                    (context.weightLostKg.takeIf { it > 0 }?.let {
                        "Vous avez déjà perdu ${"%.1f".format(it)} kg — excellent progrès. "
                    } ?: "") +
                    "La régularité (${context.currentStreak} jours de série) est plus importante que la perfection."

            else ->
                "Je suis votre coach OneFast. " +
                    "Contexte : plan ${context.fastingPlan}, ${context.fastingStatus.lowercase()}. " +
                    "Posez-moi une question précise (faim, café, que manger, fatigue…). " +
                    "Pour des réponses IA avancées, ajoutez OPENAI_API_KEY dans local.properties."
        }
    }
}
