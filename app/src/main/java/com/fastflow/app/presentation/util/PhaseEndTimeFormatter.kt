package com.fastflow.app.presentation.util

import java.text.DateFormat
import java.util.Date

object PhaseEndTimeFormatter {
    fun format(millis: Long): String =
        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(millis))
}
