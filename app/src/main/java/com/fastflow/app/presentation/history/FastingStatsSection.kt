package com.fastflow.app.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.domain.model.UserStats
import com.fastflow.app.presentation.components.StatsCard

@Composable
fun FastingStatsSection(
    stats: UserStats,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = stringResource(R.string.current_streak),
                value = "${stats.currentStreak}",
                subtitle = stringResource(R.string.days_unit),
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = stringResource(R.string.longest_streak),
                value = "${stats.longestStreak}",
                subtitle = stringResource(R.string.days_unit),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = stringResource(R.string.stats_total_fasts),
                value = "${stats.totalFastsCompleted}",
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = stringResource(R.string.stats_total_hours),
                value = String.format("%.0f", stats.totalHoursFasted),
                subtitle = stringResource(R.string.stats_hours_unit),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        StatsCard(
            title = stringResource(R.string.stats_avg_duration),
            value = String.format("%.1f", stats.averageFastDuration),
            subtitle = stringResource(R.string.stats_hours_unit),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
