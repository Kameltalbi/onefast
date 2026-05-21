package com.fastflow.app.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.domain.history.CalendarDayCell
import com.fastflow.app.domain.history.DayFastStatus
import com.fastflow.app.presentation.theme.AccentBlue
import com.fastflow.app.presentation.theme.AccentOrange
import java.util.Calendar
import java.util.Locale

@Composable
fun FastingCalendarGrid(
    monthLabel: String,
    cells: List<CalendarDayCell>,
    selectedDayMillis: Long?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekDays = rememberWeekDayLabels()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null)
            }
            Text(
                text = monthLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                week.forEach { cell ->
                    CalendarDayCell(
                        cell = cell,
                        isSelected = cell.isCurrentMonth &&
                            cell.dayStartMillis == selectedDayMillis,
                        onClick = {
                            if (cell.isCurrentMonth && cell.dayOfMonth > 0) {
                                onDaySelected(cell.dayStartMillis)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            CalendarLegendDot(color = AccentBlue, label = stringResource(R.string.calendar_legend_done))
            CalendarLegendDot(color = AccentOrange, label = stringResource(R.string.calendar_legend_active))
        }
    }
}

@Composable
private fun CalendarDayCell(
    cell: CalendarDayCell,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!cell.isCurrentMonth || cell.dayOfMonth == 0) return@Box

        val backgroundColor = when (cell.status) {
            DayFastStatus.COMPLETED -> AccentBlue.copy(alpha = 0.85f)
            DayFastStatus.ACTIVE -> AccentOrange.copy(alpha = 0.85f)
            DayFastStatus.EMPTY -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        }

        val textColor = when (cell.status) {
            DayFastStatus.COMPLETED, DayFastStatus.ACTIVE ->
                MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onSurface
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .then(
                    if (isSelected) {
                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    } else if (cell.isToday) {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                    } else {
                        Modifier
                    }
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cell.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (cell.isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
private fun CalendarLegendDot(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun rememberWeekDayLabels(): List<String> {
    val calendar = Calendar.getInstance()
    val firstDay = calendar.firstDayOfWeek
    return (0 until 7).map { offset ->
        val day = (firstDay - 1 + offset) % 7 + 1
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, day)
        }.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            ?.take(2)
            .orEmpty()
    }
}
