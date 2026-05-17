package com.fastflow.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.presentation.components.FastingCircle
import com.fastflow.app.presentation.localization.localizedName
import com.fastflow.app.presentation.components.GiantActionButton
import com.fastflow.app.presentation.components.MotivationBanner
import com.fastflow.app.presentation.components.SecondaryActionButton
import com.fastflow.app.presentation.components.StatsCard
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFastingTypeDialog by remember { mutableStateOf(false) }

    @Suppress("UNUSED_VARIABLE")
    val tick = uiState.tick

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.nav_home),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.phaseMessage.isNotBlank()) {
            MotivationBanner(message = uiState.phaseMessage)
            Spacer(modifier = Modifier.height(20.dp))
        }

        val session = uiState.currentSession
        if (session != null && session.isActive()) {
            Text(
                text = session.fastingType.localizedName(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            FastingCircle(
                progress = session.getProgress(),
                timeElapsed = formatDuration(session.getDuration()),
                timeRemaining = formatDuration(session.getRemainingTime()),
                isFasting = session.status == FastingStatus.FASTING
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (session.status) {
                FastingStatus.FASTING -> {
                    GiantActionButton(
                        icon = Icons.Default.Pause,
                        text = stringResource(R.string.pause_fast),
                        onClick = { viewModel.pauseFasting() },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        isPulsing = false
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryActionButton(
                        icon = Icons.Default.Stop,
                        text = stringResource(R.string.stop_fast),
                        onClick = { viewModel.stopFasting() },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }
                FastingStatus.PAUSED -> {
                    GiantActionButton(
                        icon = Icons.Default.PlayArrow,
                        text = stringResource(R.string.resume_fast),
                        onClick = { viewModel.resumeFasting() },
                        isPulsing = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryActionButton(
                        icon = Icons.Default.Stop,
                        text = stringResource(R.string.stop_fast),
                        onClick = { viewModel.stopFasting() },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }
                else -> {}
            }
        } else {
            Text(
                text = stringResource(R.string.dashboard_ready),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            uiState.defaultPlan?.let { plan ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.dashboard_plan_label, plan.localizedName()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            GiantActionButton(
                icon = Icons.Default.PlayArrow,
                text = stringResource(R.string.start_fast),
                onClick = { showFastingTypeDialog = true },
                isPulsing = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = stringResource(R.string.current_streak),
                value = "${uiState.userStats.currentStreak}",
                subtitle = stringResource(R.string.days_unit),
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = stringResource(R.string.current_weight),
                value = uiState.userStats.currentWeight?.let { String.format("%.1f", it) } ?: "—",
                subtitle = stringResource(R.string.kg),
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showFastingTypeDialog) {
        FastingTypeDialog(
            onDismiss = { showFastingTypeDialog = false },
            onSelectType = { type, customHours ->
                viewModel.startFasting(type, customHours)
                showFastingTypeDialog = false
            }
        )
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    return String.format("%02d:%02d", hours, minutes)
}
