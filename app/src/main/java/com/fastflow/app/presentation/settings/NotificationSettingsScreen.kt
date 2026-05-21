package com.fastflow.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val prefs = uiState.preferences

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.notif_v1_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            NotificationSwitch(
                title = stringResource(R.string.notif_toggle_all),
                checked = prefs.enabled,
                onCheckedChange = { viewModel.updatePreferences(prefs.copy(enabled = it)) }
            )
            Text(
                text = stringResource(R.string.notif_v1_start_end_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 4.dp)
            )
            NotificationSwitch(
                title = stringResource(R.string.notif_toggle_streak),
                subtitle = stringResource(R.string.notif_v1_motivation_desc),
                checked = prefs.streakMilestonesEnabled,
                enabled = prefs.enabled,
                onCheckedChange = {
                    viewModel.updatePreferences(prefs.copy(streakMilestonesEnabled = it))
                }
            )
            NotificationSwitch(
                title = stringResource(R.string.notif_toggle_hydration),
                subtitle = stringResource(R.string.hydration_reminders_desc),
                checked = prefs.hydrationEnabled,
                enabled = prefs.enabled,
                onCheckedChange = {
                    viewModel.updatePreferences(prefs.copy(hydrationEnabled = it))
                }
            )
        }
    }
}

@Composable
private fun NotificationSwitch(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    subtitle: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
