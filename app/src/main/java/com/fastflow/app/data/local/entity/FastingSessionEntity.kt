package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.model.FastingType

@Entity(tableName = "fasting_sessions")
data class FastingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val endTimeExpected: Long,
    val endTimeActual: Long? = null,
    val fastingType: String,
    val fastingHoursActual: Int,
    val status: String,
    val pausedAt: Long? = null,
    val totalPausedDuration: Long = 0
) {
    fun toDomain(): FastingSession {
        return FastingSession(
            id = id,
            startTime = startTime,
            endTimeExpected = endTimeExpected,
            endTimeActual = endTimeActual,
            fastingType = FastingType.valueOf(fastingType),
            fastingHoursActual = fastingHoursActual,
            status = FastingStatus.valueOf(status),
            pausedAt = pausedAt,
            totalPausedDuration = totalPausedDuration
        )
    }

    companion object {
        fun fromDomain(session: FastingSession): FastingSessionEntity {
            return FastingSessionEntity(
                id = session.id,
                startTime = session.startTime,
                endTimeExpected = session.endTimeExpected,
                endTimeActual = session.endTimeActual,
                fastingType = session.fastingType.name,
                fastingHoursActual = session.fastingHoursActual,
                status = session.status.name,
                pausedAt = session.pausedAt,
                totalPausedDuration = session.totalPausedDuration
            )
        }
    }
}
