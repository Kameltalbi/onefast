package com.fastflow.app.presentation.hydration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.WaterEntry
import com.fastflow.app.presentation.theme.AccentBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationScreen(
    onBack: () -> Unit,
    viewModel: HydrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val daily = uiState.daily
    var showGoalDialog by remember { mutableStateOf(false) }

    if (showGoalDialog) {
        HydrationGoalDialog(
            currentGoalMl = uiState.goalMl,
            onDismiss = { showGoalDialog = false },
            onConfirm = {
                viewModel.setGoalMl(it)
                showGoalDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.hydration_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.addGlass() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        stringResource(
                            R.string.hydration_add_glass,
                            uiState.glassSizeMl
                        )
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HydrationProgressCard(daily = daily)

            OutlinedButton(onClick = { showGoalDialog = true }) {
                Text(stringResource(R.string.hydration_edit_goal, uiState.goalMl))
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.notif_toggle_hydration),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stringResource(R.string.hydration_reminders_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                    Switch(
                        checked = uiState.remindersEnabled,
                        onCheckedChange = viewModel::setRemindersEnabled
                    )
                }
            }

            if (daily.entries.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.hydration_today_log),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                daily.entries.forEach { entry ->
                    WaterEntryRow(
                        entry = entry,
                        onDelete = { viewModel.removeEntry(entry.id) }
                    )
                }
            }
        }
    }

    uiState.error?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun HydrationProgressCard(daily: com.fastflow.app.domain.model.DailyHydration) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = daily.progress,
                    modifier = Modifier.size(160.dp),
                    strokeWidth = 12.dp,
                    color = AccentBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${(daily.progress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(
                    R.string.hydration_progress_summary,
                    daily.totalMl,
                    daily.goalMl
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(
                    R.string.hydration_glasses_count,
                    daily.glassesConsumed,
                    daily.glassesGoal
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            if (daily.remainingMl > 0) {
                Text(
                    text = stringResource(R.string.hydration_remaining, daily.remainingMl),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.hydration_goal_reached),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WaterEntryRow(entry: WaterEntry, onDelete: () -> Unit) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = AccentBlue
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.hydration_entry_format, entry.amountMl),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = timeFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HydrationGoalDialog(
    currentGoalMl: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goal by remember { mutableIntStateOf(currentGoalMl) }
    val presets = listOf(1500, 2000, 2500, 3000)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hydration_goal_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                presets.forEach { preset ->
                    FilterChip(
                        selected = goal == preset,
                        onClick = { goal = preset },
                        label = { Text(stringResource(R.string.hydration_goal_preset, preset)) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(goal) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
