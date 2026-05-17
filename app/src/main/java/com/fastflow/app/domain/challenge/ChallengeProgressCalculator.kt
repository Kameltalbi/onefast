package com.fastflow.app.domain.challenge

import com.fastflow.app.domain.model.ChallengeDefinition
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeProgressCalculator @Inject constructor() {

    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun countValidDays(
        sessions: List<FastingSession>,
        definition: ChallengeDefinition,
        startedAt: Long
    ): Int {
        val windowEnd = startedAt + TimeUnit.DAYS.toMillis(definition.durationDays.toLong())
        val validDayKeys = mutableSetOf<String>()

        for (session in sessions) {
            if (session.status != FastingStatus.COMPLETED) continue
            val end = session.endTimeActual ?: continue
            if (end < startedAt || end > windowEnd) continue

            val durationMs = end - session.startTime - session.totalPausedDuration
            val hours = durationMs / 3_600_000f
            if (hours < definition.minFastingHours) continue

            validDayKeys.add(dayFormat.format(Date(end)))
        }

        return validDayKeys.size.coerceAtMost(definition.durationDays)
    }

    fun hasExpired(startedAt: Long, durationDays: Int): Boolean {
        val windowEnd = startedAt + TimeUnit.DAYS.toMillis(durationDays.toLong())
        return System.currentTimeMillis() > windowEnd
    }

    fun isCompleted(completedDays: Int, durationDays: Int): Boolean =
        completedDays >= durationDays
}
