package com.fastflow.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.domain.model.FastingType

@Composable
fun FastingTypeDialog(
    onDismiss: () -> Unit,
    onSelectType: (FastingType, Int?) -> Unit
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
        title = { Text("Choisir un plan de jeûne") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                FastingType.v1Plans.forEach { type ->
                    TextButton(
                        onClick = {
                            if (type == FastingType.CUSTOM) {
                                showCustomDialog = true
                            } else {
                                onSelectType(type, null)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = type.formatPlanSummary(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
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
        title = { Text("Plan personnalisé") },
        text = {
            Column {
                Text(
                    text = "Combien d'heures souhaitez-vous jeûner ?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = fastingHours,
                    onValueChange = { fastingHours = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Heures de jeûne (1-23)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                val hours = fastingHours.toIntOrNull()
                if (hours != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fenêtre repas : ${24 - hours.coerceIn(1, 23)}h",
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
                Text("Démarrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
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
                        text = "Démarrage automatique",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lance le jeûne à l'heure choisie",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }

            if (enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Heure : ${String.format("%02d:%02d", hour, minute)}",
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
