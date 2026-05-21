package com.fastflow.app.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.presentation.components.StatsCard
import com.fastflow.app.presentation.components.UpgradeBanner
import com.fastflow.app.presentation.localization.getLabel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastingHistoryScreen(
    onBack: (() -> Unit)? = null,
    onOpenPricing: () -> Unit = {},
    viewModel: FastingHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.nav_history),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.isHistoryLimited) {
                        item {
                            UpgradeBanner(
                                message = stringResource(R.string.pricing_history_limited),
                                onUpgradeClick = onOpenPricing
                            )
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.stats_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (uiState.hasAdvancedStats) {
                            FastingStatsSection(stats = uiState.stats)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatsCard(
                                    title = stringResource(R.string.current_streak),
                                    value = "${uiState.stats.currentStreak}",
                                    subtitle = stringResource(R.string.days_unit),
                                    modifier = Modifier.weight(1f)
                                )
                                StatsCard(
                                    title = stringResource(R.string.stats_total_fasts),
                                    value = "${uiState.stats.totalFastsCompleted}",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            UpgradeBanner(
                                message = stringResource(R.string.pricing_upgrade_pro),
                                onUpgradeClick = onOpenPricing
                            )
                        }
                    }

                    if (uiState.hasCalendar) {
                        item {
                            Text(
                                text = stringResource(R.string.calendar_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FastingCalendarGrid(
                                monthLabel = uiState.monthLabel,
                                cells = uiState.calendarCells,
                                selectedDayMillis = uiState.selectedDayMillis,
                                onPreviousMonth = viewModel::previousMonth,
                                onNextMonth = viewModel::nextMonth,
                                onDaySelected = viewModel::selectDay
                            )
                        }
                    }

                    item {
                        val sectionTitle = if (uiState.selectedDayMillis != null) {
                            stringResource(R.string.history_day_sessions)
                        } else {
                            stringResource(R.string.history_recent)
                        }
                        Text(
                            text = sectionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val sessions = uiState.visibleSessions
                    if (sessions.isEmpty()) {
                        item {
                            Text(
                                text = if (uiState.allSessions.isEmpty()) {
                                    stringResource(R.string.history_empty)
                                } else {
                                    stringResource(R.string.history_day_empty)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        items(sessions) { session ->
                            HistorySessionCard(session)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySessionCard(session: FastingSession) {
    val context = LocalContext.current
    val durationMs = session.endTimeActual?.let {
        it - session.startTime - session.totalPausedDuration
    } ?: 0L
    val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = session.fastingType.getLabel(context),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateFormat.format(Date(session.startTime)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.history_duration, hours, minutes),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
