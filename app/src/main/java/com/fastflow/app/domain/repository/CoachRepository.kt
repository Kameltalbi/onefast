package com.fastflow.app.domain.repository

import com.fastflow.app.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface CoachRepository {
    fun observeMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(userMessage: String): Result<ChatMessage>
    suspend fun clearHistory()
    suspend fun getRemainingFreeQuestions(): Int
    suspend fun isPremiumUser(): Boolean
}
