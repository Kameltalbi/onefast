package com.fastflow.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.domain.model.HealthAlert
import com.fastflow.app.domain.model.HealthAlertSeverity

@Composable
fun HealthAlertsSection(
    alerts: List<HealthAlert>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (alerts.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        alerts.forEach { alert ->
            HealthAlertCard(alert = alert, onDismiss = { onDismiss(alert.id) })
        }
    }
}

@Composable
private fun HealthAlertCard(
    alert: HealthAlert,
    onDismiss: () -> Unit
) {
    val containerColor = when (alert.severity) {
        HealthAlertSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
        HealthAlertSeverity.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        HealthAlertSeverity.INFO -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (alert.severity) {
        HealthAlertSeverity.CRITICAL -> MaterialTheme.colorScheme.onErrorContainer
        HealthAlertSeverity.WARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        HealthAlertSeverity.INFO -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = alert.recommendation,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.health_alert_dismiss),
                    tint = contentColor
                )
            }
        }
    }
}
