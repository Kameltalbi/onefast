package com.fastflow.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.SubscriptionCapabilities
import com.fastflow.app.domain.model.SubscriptionFeature
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.presentation.localization.localizedName
import com.fastflow.app.presentation.localization.localizedPlanSummary

@Composable
fun FastingTypeDialog(
    subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    onDismiss: () -> Unit,
    onSelectType: (FastingType, Int?) -> Unit,
    onUpgradeClick: () -> Unit = {}
) {
    var showCustomDialog by remember { mutableStateOf(false) }

    if (showCustomDialog) {
        CustomPlanDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { hours ->
                onSelectType(FastingType.CUSTOM, hours)
                showCustomDialog = false
                onDismiss()
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_choose_plan)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                FastingType.v1Plans.forEach { type ->
                    val freePlans = setOf(
                        FastingType.TWELVE_TWELVE,
                        FastingType.FOURTEEN_TEN,
                        FastingType.SIXTEEN_EIGHT
                    )
                    val locked = !SubscriptionCapabilities.hasAccess(
                        subscriptionTier,
                        SubscriptionFeature.MULTIPLE_PLANS
                    ) && type !in freePlans

                    TextButton(
                        onClick = {
                            if (locked) {
                                onUpgradeClick()
                                onDismiss()
                            } else if (type == FastingType.CUSTOM) {
                                showCustomDialog = true
                            } else {
                                onSelectType(type, null)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = type.localizedName(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = type.localizedPlanSummary(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            if (locked) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = stringResource(R.string.pricing_plan_locked),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun CustomPlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var fastingHours by remember { mutableStateOf("16") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_custom_plan)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.dialog_custom_plan_question),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = fastingHours,
                    onValueChange = { fastingHours = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text(stringResource(R.string.dialog_fasting_hours_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                val hours = fastingHours.toIntOrNull()
                if (hours != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.dialog_eating_window_hours,
                            24 - hours.coerceIn(1, 23)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    fastingHours.toIntOrNull()?.let { h ->
                        if (h in 1..23) onConfirm(h)
                    }
                },
                enabled = fastingHours.toIntOrNull() in 1..23
            ) {
                Text(stringResource(R.string.start_fast))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoStartSettingsCard(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onEnabledChange: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.dialog_auto_start_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.dialog_auto_start_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }

            if (enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        R.string.dialog_time_label,
                        String.format("%02d:%02d", hour, minute)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(19 to 0, 20 to 0, 21 to 0, 22 to 0).forEach { (h, m) ->
                        FilterChip(
                            selected = hour == h && minute == m,
                            onClick = { onTimeChange(h, m) },
                            label = { Text(String.format("%02d:%02d", h, m)) }
                        )
                    }
                }
            }
        }
    }
}
