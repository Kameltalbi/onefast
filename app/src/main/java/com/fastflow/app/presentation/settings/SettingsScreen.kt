package com.fastflow.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.presentation.components.LanguageSelector
import com.fastflow.app.presentation.dashboard.AutoStartSettingsCard
import com.fastflow.app.presentation.dashboard.FastingTypeDialog
import com.fastflow.app.presentation.localization.localizedName
import com.fastflow.app.presentation.localization.localizedPlanSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPricing: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as Activity
    var showPlanDialog by remember { mutableStateOf(false) }

    if (showPlanDialog) {
        FastingTypeDialog(
            subscriptionTier = uiState.subscriptionTier,
            onDismiss = { showPlanDialog = false },
            onSelectType = { type, customHours ->
                viewModel.setDefaultPlan(type, customHours)
                showPlanDialog = false
            },
            onUpgradeClick = onOpenPricing
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
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
                stringResource(R.string.settings_section_app),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            LanguageSelector(
                selectedTag = uiState.languageTag,
                onSelect = { viewModel.selectLanguage(it, activity) }
            )

            SettingsNavCard(
                title = stringResource(R.string.notifications_settings),
                subtitle = stringResource(R.string.profile_notifications_desc),
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                onClick = onOpenNotifications
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.settings_section_fasting),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            uiState.defaultPlan?.let { plan ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.profile_default_plan),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            plan.localizedName(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            plan.localizedPlanSummary(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showPlanDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.settings_change_plan))
                        }
                    }
                }
            } ?: OutlinedButton(
                onClick = { showPlanDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.settings_change_plan))
            }

            AutoStartSettingsCard(
                enabled = uiState.autoStartEnabled,
                hour = uiState.autoStartHour,
                minute = uiState.autoStartMinute,
                onEnabledChange = viewModel::setAutoStartEnabled,
                onTimeChange = viewModel::setAutoStartTime
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsNavCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
