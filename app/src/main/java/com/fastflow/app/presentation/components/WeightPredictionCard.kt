package com.fastflow.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.domain.model.WeightPrediction
import com.fastflow.app.presentation.theme.AccentBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeightPredictionCard(
    prediction: WeightPrediction,
    onSetGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AccentBlue.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = AccentBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.prediction_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onSetGoal) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (prediction.targetWeightKg != null) {
                            stringResource(R.string.prediction_edit_goal)
                        } else {
                            stringResource(R.string.prediction_set_goal)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = prediction.insightMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            if (prediction.currentWeightKg != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    prediction.projectedWeightIn4Weeks?.let { projected ->
                        PredictionStatChip(
                            label = stringResource(R.string.prediction_in_4_weeks),
                            value = "${String.format("%.1f", projected)} kg",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    PredictionStatChip(
                        label = stringResource(R.string.prediction_weekly_rate),
                        value = "${String.format("%.1f", prediction.weeklyLossRateKg)} kg",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PredictionStatChip(
                    label = stringResource(R.string.prediction_recommended_plan),
                    value = prediction.recommendedPlan.displayName,
                    modifier = Modifier.fillMaxWidth()
                )

                prediction.estimatedGoalDateMillis?.let { dateMillis ->
                    Spacer(modifier = Modifier.height(8.dp))
                    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.FRENCH).format(Date(dateMillis))
                    Text(
                        text = stringResource(R.string.prediction_goal_date, dateStr),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
