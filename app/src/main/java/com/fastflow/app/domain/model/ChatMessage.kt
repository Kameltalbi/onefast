package com.fastflow.app.domain.model

enum class ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val id: Int = 0,
    val role: ChatRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
