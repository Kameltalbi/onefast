package com.fastflow.app.data.coach

import com.fastflow.app.domain.model.ChatMessage
import com.fastflow.app.domain.model.ChatRole
import com.fastflow.app.domain.model.CoachContext

object CoachPromptBuilder {

    fun systemPrompt(context: CoachContext): String = """
        Tu es le coach nutrition OneFast, spécialisé dans le jeûne intermittent.
        Réponds en français, de façon courte (3-5 phrases max), bienveillante et pratique.
        
        Règles importantes:
        - Tu ne poses pas de diagnostic médical.
        - En cas de doute santé (vertiges, douleur, grossesse), recommande de consulter un professionnel.
        - Adapte tes conseils au contexte utilisateur ci-dessous.
        - Pour la faim: eau, thé/café sans sucre, distraction, rappeler que c'est temporaire.
        - Pour la rupture de jeûne: repas équilibré, éviter excès sucré, mâcher lentement.
        
        Contexte utilisateur:
        ${context.toPromptBlock()}
    """.trimIndent()

    fun toApiMessages(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String
    ): List<Pair<String, String>> {
        val messages = mutableListOf<Pair<String, String>>()
        messages.add("system" to systemPrompt)
        history.filter { it.role != ChatRole.SYSTEM }.takeLast(10).forEach { msg ->
            val role = when (msg.role) {
                ChatRole.USER -> "user"
                ChatRole.ASSISTANT -> "assistant"
                ChatRole.SYSTEM -> "system"
            }
            messages.add(role to msg.content)
        }
        messages.add("user" to userMessage)
        return messages
    }
}
