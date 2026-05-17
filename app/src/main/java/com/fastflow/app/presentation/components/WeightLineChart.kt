package com.fastflow.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.domain.model.WeightEntry
import com.fastflow.app.presentation.theme.AccentBlue

@Composable
fun WeightLineChart(
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    val sorted = entries.sortedBy { it.timestamp }.takeLast(14)
    if (sorted.size < 2) return

    val weights = sorted.map { it.getWeightInKg() }
    val minW = weights.min() - 1f
    val maxW = weights.max() + 1f
    val range = (maxW - minW).coerceAtLeast(1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Évolution du poids",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${String.format("%.1f", weights.first())} → ${String.format("%.1f", weights.last())} kg",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val path = Path()
                sorted.forEachIndexed { index, entry ->
                    val x = if (sorted.size == 1) size.width / 2 else {
                        index.toFloat() / (sorted.size - 1) * size.width
                    }
                    val y = size.height - ((entry.getWeightInKg() - minW) / range) * size.height
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path,
                    color = AccentBlue,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
                sorted.forEachIndexed { index, entry ->
                    val x = index.toFloat() / (sorted.size - 1) * size.width
                    val y = size.height - ((entry.getWeightInKg() - minW) / range) * size.height
                    drawCircle(color = AccentBlue, radius = 6f, center = Offset(x, y))
                }
            }
        }
    }
}
