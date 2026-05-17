package com.fastflow.app.data.repository

import com.fastflow.app.data.coach.CoachContextBuilder
import com.fastflow.app.data.coach.CoachPromptBuilder
import com.fastflow.app.data.coach.LocalCoachResponder
import com.fastflow.app.data.coach.OpenAiCoachService
import com.fastflow.app.data.local.dao.ChatMessageDao
import com.fastflow.app.data.local.entity.ChatMessageEntity
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.ChatMessage
import com.fastflow.app.domain.model.ChatRole
import com.fastflow.app.domain.model.CoachQuota
import com.fastflow.app.domain.repository.CoachRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoachRepositoryImpl @Inject constructor(
    private val chatDao: ChatMessageDao,
    private val contextBuilder: CoachContextBuilder,
    private val openAiService: OpenAiCoachService,
    private val localResponder: LocalCoachResponder,
    private val preferencesManager: PreferencesManager
) : CoachRepository {

    override fun observeMessages(): Flow<List<ChatMessage>> =
        chatDao.observeMessages().map { list -> list.map { it.toDomain() } }

    override suspend fun sendMessage(userMessage: String): Result<ChatMessage> {
        val trimmed = userMessage.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Message vide"))
        }

        if (!preferencesManager.isPremiumUserOnce() &&
            preferencesManager.getRemainingCoachQuestionsOnce() <= 0
        ) {
            return Result.failure(
                Exception("Limite gratuite atteinte (${CoachQuota.FREE_DAILY_LIMIT} questions/jour). Passez Premium pour l'illimité.")
            )
        }

        val userEntity = ChatMessageEntity(
            role = ChatRole.USER.name,
            content = trimmed,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insert(userEntity)

        if (isFatigueRelated(trimmed)) {
            preferencesManager.recordFatigueMention()
        }

        val context = contextBuilder.build()
        val history = chatDao.getAllMessages().map { it.toDomain() }
        val systemPrompt = CoachPromptBuilder.systemPrompt(context)

        val assistantContent = if (openAiService.isConfigured()) {
            val apiMessages = CoachPromptBuilder.toApiMessages(systemPrompt, history, trimmed)
            openAiService.ask(apiMessages).getOrElse { error ->
                localResponder.respond(trimmed, context) +
                    "\n\n(Mode local — API: ${error.message})"
            }
        } else {
            localResponder.respond(trimmed, context)
        }

        val assistantEntity = ChatMessageEntity(
            role = ChatRole.ASSISTANT.name,
            content = assistantContent,
            timestamp = System.currentTimeMillis()
        )
        val id = chatDao.insert(assistantEntity)

        if (!preferencesManager.isPremiumUserOnce()) {
            preferencesManager.incrementCoachQuestionCount()
        }

        return Result.success(
            ChatMessage(
                id = id.toInt(),
                role = ChatRole.ASSISTANT,
                content = assistantContent,
                timestamp = assistantEntity.timestamp
            )
        )
    }

    override suspend fun clearHistory() {
        chatDao.clearAll()
    }

    override suspend fun getRemainingFreeQuestions(): Int =
        preferencesManager.getRemainingCoachQuestionsOnce()

    override suspend fun isPremiumUser(): Boolean =
        preferencesManager.isPremiumUserOnce()

    private fun isFatigueRelated(message: String): Boolean {
        val lower = message.lowercase()
        return listOf("fatigu", "épuis", "epuis", "faible", "vertige", "malaise", "trembl")
            .any { lower.contains(it) }
    }
}
