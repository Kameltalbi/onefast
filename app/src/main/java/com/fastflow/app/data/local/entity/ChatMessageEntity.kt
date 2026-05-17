package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.ChatMessage
import com.fastflow.app.domain.model.ChatRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val role: String,
    val content: String,
    val timestamp: Long
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        role = ChatRole.valueOf(role),
        content = content,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(message: ChatMessage): ChatMessageEntity = ChatMessageEntity(
            id = message.id,
            role = message.role.name,
            content = message.content,
            timestamp = message.timestamp
        )
    }
}
