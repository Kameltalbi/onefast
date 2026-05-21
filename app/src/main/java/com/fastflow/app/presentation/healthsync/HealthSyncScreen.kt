package com.fastflow.app.presentation.healthsync

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.HealthConnectStatus
import com.fastflow.app.domain.model.HealthSyncSnapshot
import com.fastflow.app.domain.model.SubscriptionCapabilities
import com.fastflow.app.domain.model.SubscriptionFeature
import com.fastflow.app.presentation.components.HealthMetricTile
import com.fastflow.app.presentation.components.UpgradeBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthSyncScreen(
    onBack: () -> Unit,
    onOpenPricing: () -> Unit = {},
    viewModel: HealthSyncViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted -> viewModel.onPermissionsResult(granted) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.health_sync_title), fontWeight = FontWeight.Bold) },
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
            Text(
                stringResource(R.string.health_sync_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            if (!SubscriptionCapabilities.hasAccess(
                    uiState.subscriptionTier,
                    SubscriptionFeature.HEALTH_SYNC
                )
            ) {
                UpgradeBanner(
                    message = stringResource(R.string.pricing_upgrade_premium),
                    onUpgradeClick = onOpenPricing
                )
            } else when (uiState.connectStatus) {
                HealthConnectStatus.NOT_INSTALLED -> StatusCard(
                    title = stringResource(R.string.health_sync_not_installed),
                    actionLabel = stringResource(R.string.health_sync_install),
                    onAction = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(
                                    "https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"
                                )
                            }
                        )
                    }
                )
                HealthConnectStatus.UPDATE_REQUIRED -> StatusCard(
                    title = stringResource(R.string.health_sync_update_required),
                    actionLabel = stringResource(R.string.health_sync_install),
                    onAction = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(
                                    "https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"
                                )
                            }
                        )
                    }
                )
                HealthConnectStatus.NOT_SUPPORTED -> StatusCard(
                    title = stringResource(R.string.health_sync_not_supported),
                    actionLabel = null,
                    onAction = {}
                )
                HealthConnectStatus.AVAILABLE -> {
                    if (!uiState.hasAllPermissions) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(uiState.requiredPermissions)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.health_sync_connect))
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.syncNow() },
                                enabled = !uiState.isSyncing,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.health_sync_now))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.writeWeightEnabled,
                            onCheckedChange = viewModel::setWriteWeightEnabled
                        )
                        Text(stringResource(R.string.health_sync_write_weight))
                    }
                }
            }

            uiState.error?.let { error ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(error, modifier = Modifier.padding(12.dp))
                }
            }

            uiState.snapshot?.let { snapshot ->
                if (snapshot.hasPermissions) {
                    HealthMetricsGrid(snapshot)
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            actionLabel?.let { label ->
                Spacer(Modifier.height(12.dp))
                Button(onClick = onAction) { Text(label) }
            }
        }
    }
}

@Composable
private fun HealthMetricsGrid(snapshot: HealthSyncSnapshot) {
    Text(stringResource(R.string.health_sync_metrics), fontWeight = FontWeight.Bold)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HealthMetricTile(
                icon = Icons.Default.DirectionsWalk,
                label = stringResource(R.string.health_sync_steps),
                value = "${snapshot.stepsToday}",
                subtitle = stringResource(R.string.health_sync_steps_avg, snapshot.stepsWeekAvg),
                modifier = Modifier.weight(1f)
            )
            HealthMetricTile(
                icon = Icons.Default.LocalFireDepartment,
                label = stringResource(R.string.health_sync_calories),
                value = "${snapshot.activeCaloriesToday.toInt()}",
                subtitle = "kcal",
                modifier = Modifier.weight(1f)
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HealthMetricTile(
                icon = Icons.Default.Bedtime,
                label = stringResource(R.string.health_sync_sleep),
                value = snapshot.sleepLastNightHours?.let { "${it}h" } ?: "—",
                subtitle = stringResource(R.string.health_sync_last_night),
                modifier = Modifier.weight(1f)
            )
            HealthMetricTile(
                icon = Icons.Default.Favorite,
                label = stringResource(R.string.health_sync_heart),
                value = snapshot.restingHeartRate?.let { "$it" } ?: "—",
                subtitle = "bpm",
                modifier = Modifier.weight(1f)
            )
        }
        snapshot.weightFromHealthKg?.let { weight ->
            HealthMetricTile(
                icon = Icons.Default.MonitorWeight,
                label = stringResource(R.string.health_sync_weight),
                value = String.format("%.1f kg", weight),
                subtitle = stringResource(R.string.health_sync_from_hc),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
