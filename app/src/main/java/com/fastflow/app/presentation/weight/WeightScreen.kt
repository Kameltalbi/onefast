package com.fastflow.app.presentation.weight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.presentation.components.OneFastLogo
import com.fastflow.app.presentation.components.OneFastLogoVariant
import com.fastflow.app.presentation.components.StatsCard
import com.fastflow.app.presentation.components.WeightLineChart
import com.fastflow.app.presentation.dashboard.SetWeightGoalDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    viewModel: WeightViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_weight))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OneFastLogo(
                variant = OneFastLogoVariant.Full,
                width = 160.dp,
                height = 70.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = stringResource(R.string.current_weight),
                    value = uiState.currentWeight?.let { String.format("%.1f", it) }
                        ?: "—",
                    subtitle = stringResource(R.string.kg),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = stringResource(R.string.weight_goal),
                    value = uiState.targetWeightKg?.let { String.format("%.1f", it) } ?: "—",
                    subtitle = stringResource(R.string.kg),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = { viewModel.showGoalDialog() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (uiState.targetWeightKg != null) {
                        stringResource(R.string.prediction_edit_goal)
                    } else {
                        stringResource(R.string.prediction_set_goal)
                    }
                )
            }

            if (uiState.weightHistory.size >= 2) {
                WeightLineChart(entries = uiState.weightHistory)
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.progress_chart_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AddWeightDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { weight, _ -> viewModel.addWeight(weight) }
        )
    }

    if (uiState.showGoalDialog) {
        SetWeightGoalDialog(
            currentWeight = uiState.currentWeight,
            currentTarget = uiState.targetWeightKg,
            onDismiss = { viewModel.hideGoalDialog() },
            onConfirm = { viewModel.setTargetWeight(it) },
            onClear = { viewModel.clearTargetWeight() }
        )
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

@Composable
fun AddWeightDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float, Float?) -> Unit
) {
    var weightText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_weight)) },
        text = {
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text(stringResource(R.string.kg)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    weightText.toFloatOrNull()?.let { onConfirm(it, null) }
                },
                enabled = weightText.toFloatOrNull()?.let { it > 0 } == true
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
