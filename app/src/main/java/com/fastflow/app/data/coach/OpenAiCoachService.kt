package com.fastflow.app.data.coach

import com.fastflow.app.BuildConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAiCoachService @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun isConfigured(): Boolean = BuildConfig.OPENAI_API_KEY.isNotBlank()

    suspend fun ask(
        messages: List<Pair<String, String>>,
        model: String = "gpt-4o-mini",
        maxTokens: Int = 400
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.OPENAI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Clé API non configurée"))
        }

        try {
            val body = ChatRequest(
                model = model,
                messages = messages.map { ChatMessageDto(role = it.first, content = it.second) },
                maxTokens = maxTokens,
                temperature = 0.7
            )

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(gson.toJson(body).toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
                ?: return@withContext Result.failure(Exception("Réponse vide"))

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("API erreur ${response.code}: $responseBody"))
            }

            val parsed = gson.fromJson(responseBody, ChatResponse::class.java)
            val content = parsed.choices?.firstOrNull()?.message?.content
                ?: return@withContext Result.failure(Exception("Réponse invalide"))

            Result.success(content.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private data class ChatRequest(
        val model: String,
        val messages: List<ChatMessageDto>,
        @SerializedName("max_tokens") val maxTokens: Int,
        val temperature: Double
    )

    private data class ChatMessageDto(val role: String, val content: String)

    private data class ChatResponse(val choices: List<Choice>?)

    private data class Choice(val message: MessageContent?)

    private data class MessageContent(val content: String?)
}
