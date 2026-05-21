package com.fastflow.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingWeightEstimate

@Composable
fun FastingWeightConfirmDialog(
    estimate: FastingWeightEstimate,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var weightText by remember(estimate.estimatedWeightKg) {
        mutableStateOf(String.format("%.1f", estimate.estimatedWeightKg))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.weight_prompt_after_fast_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(
                        R.string.weight_prompt_estimate_summary,
                        estimate.fastingHours,
                        estimate.caloriesBurned,
                        estimate.kgLost
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.weight_prompt_previous),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stringResource(
                                R.string.weight_value_kg,
                                estimate.previousWeightKg
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.weight_prompt_estimated),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stringResource(
                                R.string.weight_value_kg,
                                estimate.estimatedWeightKg
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.weight_prompt_confirm_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text(stringResource(R.string.weight_prompt_weigh_in_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { weightText.toFloatOrNull()?.let(onConfirm) },
                enabled = weightText.toFloatOrNull()?.let { it in 40f..250f } == true
            ) {
                Text(stringResource(R.string.weight_prompt_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.weight_prompt_skip))
            }
        }
    )
}
