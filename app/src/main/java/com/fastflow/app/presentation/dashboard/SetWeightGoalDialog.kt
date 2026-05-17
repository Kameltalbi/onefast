package com.fastflow.app.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
        title = { Text("Objectif de poids") },
        text = {
            Column {
                Text(
                    text = "Définissez le poids que vous souhaitez atteindre.",
                    style = MaterialTheme.typography.bodyMedium
                )
                currentWeight?.let {
                    Text(
                        text = "Poids actuel : ${String.format("%.1f", it)} kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Objectif (kg)") },
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
                Text("Enregistrer")
            }
        },
        dismissButton = {
            Row {
                if (currentTarget != null) {
                    TextButton(onClick = onClear) {
                        Text("Supprimer")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    )
}
