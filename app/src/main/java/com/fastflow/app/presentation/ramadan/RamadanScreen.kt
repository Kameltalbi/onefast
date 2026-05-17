package com.fastflow.app.presentation.ramadan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.ramadan.RamadanNextEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamadanScreen(
    onBack: () -> Unit,
    onOpenChallenges: () -> Unit = {},
    viewModel: RamadanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var city by remember(uiState.settings.city) { mutableStateOf(uiState.settings.city) }
    var country by remember(uiState.settings.country) { mutableStateOf(uiState.settings.country) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

  @Suppress("UNUSED_VARIABLE")
    val tick = uiState.tick

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ramadan_title), fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.ramadan_mode_label),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.ramadan_mode_desc),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = uiState.settings.enabled,
                        onCheckedChange = viewModel::setEnabled
                    )
                }
            }

            if (uiState.settings.enabled) {
                OutlinedTextField(
                    value = city,
                    onValueChange = {
                        city = it
                        viewModel.updateLocation(it, country)
                    },
                    label = { Text(stringResource(R.string.ramadan_city)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = {
                        country = it
                        viewModel.updateLocation(city, it)
                    },
                    label = { Text(stringResource(R.string.ramadan_country)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = viewModel::saveSettings,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.ramadan_refresh_times))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.settings.hydrationRemindersEnabled,
                        onCheckedChange = viewModel::setHydrationReminders
                    )
                    Text(stringResource(R.string.ramadan_hydration_reminders))
                }

                uiState.timings?.let { timings ->
                    val hijri = timings.hijriDay?.let { day ->
                        timings.hijriMonth?.let { month -> "$day $month" } ?: day
                    }
                    hijri?.let {
                        Text(
                            stringResource(R.string.ramadan_hijri_date, it),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    CountdownCard(
                        nextEvent = uiState.nextEvent(),
                        remainingMillis = uiState.millisUntilNextEvent(),
                        isFasting = uiState.isFasting
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimingCard(
                            icon = Icons.Default.WbSunny,
                            label = stringResource(R.string.ramadan_iftar),
                            time = timeFormat.format(Date(timings.maghribMillis)),
                            modifier = Modifier.weight(1f)
                        )
                        TimingCard(
                            icon = Icons.Default.NightsStay,
                            label = stringResource(R.string.ramadan_suhoor),
                            time = timeFormat.format(Date(timings.fajrMillis)),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (uiState.isFasting) {
                        Button(
                            onClick = viewModel::startRamadanFast,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.ramadan_start_fast))
                        }
                    }
                }

                Text(stringResource(R.string.ramadan_tips_title), fontWeight = FontWeight.Bold)
                val tips = stringArrayResource(R.array.ramadan_tips)
                tips.forEach { tip ->
                    Text("• $tip", style = MaterialTheme.typography.bodyMedium)
                }

                OutlinedButton(
                    onClick = onOpenChallenges,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.ramadan_open_challenge))
                }
            }

            uiState.error?.let { error ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(error, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CountdownCard(
    nextEvent: RamadanNextEvent,
    remainingMillis: Long,
    isFasting: Boolean
) {
    val label = when (nextEvent) {
        RamadanNextEvent.IFTAR -> stringResource(R.string.ramadan_until_iftar)
        RamadanNextEvent.SUHOOR -> stringResource(R.string.ramadan_until_suhoor)
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isFasting) stringResource(R.string.ramadan_status_fasting)
                else stringResource(R.string.ramadan_status_eating),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                formatCountdown(remainingMillis),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TimingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(time, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatCountdown(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}
