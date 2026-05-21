package com.fastflow.app.domain.history

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import java.util.Calendar
import java.util.Locale

enum class DayFastStatus {
    EMPTY,
    COMPLETED,
    ACTIVE
}

data class CalendarDayCell(
    val dayOfMonth: Int,
    val dayStartMillis: Long,
    val status: DayFastStatus,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

data class FastingMonthCalendar(
    val year: Int,
    val month: Int,
    val monthLabel: String,
    val cells: List<CalendarDayCell>
)

object FastingCalendarBuilder {

    fun buildMonth(
        year: Int,
        month: Int,
        completedSessions: List<FastingSession>,
        activeSession: FastingSession?,
        locale: Locale = Locale.getDefault()
    ): FastingMonthCalendar {
        val completedDays = completedSessions
            .filter { it.status == FastingStatus.COMPLETED }
            .map { startOfDayMillis(it.startTime) }
            .toSet()

        val monthCal = Calendar.getInstance(locale).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstWeekday = (monthCal.get(Calendar.DAY_OF_WEEK) - monthCal.firstDayOfWeek + 7) % 7
        val todayStart = startOfDayMillis(System.currentTimeMillis())

        val activeDayStart = activeSession?.takeIf { it.isActive() }?.let {
            startOfDayMillis(it.startTime)
        }

        val cells = mutableListOf<CalendarDayCell>()

        repeat(firstWeekday) {
            cells.add(
                CalendarDayCell(
                    dayOfMonth = 0,
                    dayStartMillis = 0L,
                    status = DayFastStatus.EMPTY,
                    isCurrentMonth = false,
                    isToday = false
                )
            )
        }

        for (day in 1..daysInMonth) {
            monthCal.set(Calendar.DAY_OF_MONTH, day)
            val dayStart = monthCal.timeInMillis
            val status = when {
                dayStart == activeDayStart -> DayFastStatus.ACTIVE
                dayStart in completedDays -> DayFastStatus.COMPLETED
                else -> DayFastStatus.EMPTY
            }
            cells.add(
                CalendarDayCell(
                    dayOfMonth = day,
                    dayStartMillis = dayStart,
                    status = status,
                    isCurrentMonth = true,
                    isToday = dayStart == todayStart
                )
            )
        }

        return FastingMonthCalendar(
            year = year,
            month = month,
            monthLabel = monthCal.getDisplayName(Calendar.MONTH, Calendar.LONG_STANDALONE, locale)
                ?.replaceFirstChar { c -> c.uppercase(locale) } + " $year",
            cells = cells
        )
    }

    fun startOfDayMillis(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun sessionsOnDay(sessions: List<FastingSession>, dayStartMillis: Long): List<FastingSession> {
        val dayEnd = dayStartMillis + 24 * 60 * 60 * 1000L
        return sessions.filter { it.startTime in dayStartMillis until dayEnd }
    }
}
