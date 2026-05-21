package com.fastflow.app.presentation.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.presentation.components.OneFastTopBarLogo

data class MoreMenuEntry(
    val route: String,
    val titleRes: Int,
    val subtitleRes: Int,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit
) {
    val entries = listOf(
        MoreMenuEntry(
            route = "challenges",
            titleRes = R.string.challenges_title,
            subtitleRes = R.string.challenges_available_title,
            icon = Icons.Default.EmojiEvents
        ),
        MoreMenuEntry(
            route = "community",
            titleRes = R.string.community_title,
            subtitleRes = R.string.community_sync_hint,
            icon = Icons.Default.Groups
        ),
        MoreMenuEntry(
            route = "history",
            titleRes = R.string.nav_history,
            subtitleRes = R.string.stats_title,
            icon = Icons.Default.History
        ),
        MoreMenuEntry(
            route = "tips",
            titleRes = R.string.tips_title,
            subtitleRes = R.string.tips_card_desc,
            icon = Icons.Default.Lightbulb
        ),
        MoreMenuEntry(
            route = "hydration",
            titleRes = R.string.hydration_title,
            subtitleRes = R.string.hydration_card_desc,
            icon = Icons.Default.WaterDrop
        ),
        MoreMenuEntry(
            route = "meal_plan",
            titleRes = R.string.meal_plan_title,
            subtitleRes = R.string.meal_plan_card_desc,
            icon = Icons.Default.RestaurantMenu
        ),
        MoreMenuEntry(
            route = "health_sync",
            titleRes = R.string.health_sync_title,
            subtitleRes = R.string.health_sync_desc,
            icon = Icons.Default.Favorite
        ),
        MoreMenuEntry(
            route = "ramadan",
            titleRes = R.string.ramadan_title,
            subtitleRes = R.string.ramadan_card_desc,
            icon = Icons.Default.NightsStay
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OneFastTopBarLogo(contentDescription = stringResource(R.string.nav_more))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.more_section_features),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            entries.forEach { entry ->
                MoreMenuCard(
                    title = stringResource(entry.titleRes),
                    subtitle = stringResource(entry.subtitleRes),
                    icon = entry.icon,
                    onClick = { onNavigate(entry.route) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
