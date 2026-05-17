package com.fastflow.app.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fastflow.app.R

@Composable
fun SetWeightGoalDialog(
    currentWeight: Float?,
    currentTarget: Float?,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit,
    onClear: () -> Unit
) {
    var weightText by remember {
        mutableStateOf(currentTarget?.toString() ?: currentWeight?.let { (it - 5f).coerceAtLeast(40f).toString() } ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.weight_goal_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.weight_goal_dialog_desc),
                    style = MaterialTheme.typography.bodyMedium
                )
                currentWeight?.let {
                    Text(
                        text = stringResource(R.string.weight_goal_current, it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text(stringResource(R.string.weight_goal_input_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    weightText.toFloatOrNull()?.let { onConfirm(it) }
                },
                enabled = weightText.toFloatOrNull()?.let { w ->
                    currentWeight == null || w < currentWeight
                } == true
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Row {
                if (currentTarget != null) {
                    TextButton(onClick = onClear) {
                        Text(stringResource(R.string.delete))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}
