package com.fastflow.app.presentation.challenges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.ChallengeDefinition
import com.fastflow.app.domain.model.ChallengeType
import com.fastflow.app.domain.model.ChallengeUiModel
import com.fastflow.app.domain.model.EarnedBadge
import com.fastflow.app.domain.model.UserChallenge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.challenges_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.challenges.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            uiState.error?.let { error ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (uiState.badges.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.challenges_badges_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    BadgesRow(badges = uiState.badges)
                }
            }

            item {
                Text(
                    stringResource(R.string.challenges_available_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.challenges) { model ->
                ChallengeCard(
                    model = model,
                    onJoin = { viewModel.joinChallenge(model.definition.type) },
                    onAbandon = { model.enrollment?.id?.let(viewModel::abandonChallenge) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.challenges_leaderboard_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun BadgesRow(badges: List<EarnedBadge>) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        badges.take(4).forEach { badge ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = badgeTitle(badge.challengeType),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    Text(
                        text = dateFormat.format(Date(badge.earnedAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    model: ChallengeUiModel,
    onJoin: () -> Unit,
    onAbandon: () -> Unit
) {
    val definition = model.definition
    val enrollment = model.enrollment

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challengeTitle(definition.type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = challengeDescription(definition.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.challenges_rule,
                    definition.durationDays,
                    definition.minFastingHours.toInt()
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            enrollment?.let { active ->
                if (active.isActive()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ChallengeProgress(enrollment = active, definition = definition)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onAbandon) {
                        Text(stringResource(R.string.challenges_abandon))
                    }
                }
            }

            if (model.canJoin) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onJoin, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.challenges_join))
                }
            }
        }
    }
}

@Composable
private fun ChallengeProgress(enrollment: UserChallenge, definition: ChallengeDefinition) {
    val progress = enrollment.progressPercent(definition) / 100f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.challenges_progress, enrollment.completedDays, definition.durationDays),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text("${enrollment.progressPercent(definition)}%", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun challengeTitle(type: ChallengeType): String = when (type) {
    ChallengeType.SEVEN_DAYS -> stringResource(R.string.challenge_7_days_title)
    ChallengeType.THIRTY_DAYS -> stringResource(R.string.challenge_30_days_title)
    ChallengeType.RAMADAN -> stringResource(R.string.challenge_ramadan_title)
    ChallengeType.SUMMER_BODY -> stringResource(R.string.challenge_summer_title)
}

@Composable
private fun challengeDescription(type: ChallengeType): String = when (type) {
    ChallengeType.SEVEN_DAYS -> stringResource(R.string.challenge_7_days_desc)
    ChallengeType.THIRTY_DAYS -> stringResource(R.string.challenge_30_days_desc)
    ChallengeType.RAMADAN -> stringResource(R.string.challenge_ramadan_desc)
    ChallengeType.SUMMER_BODY -> stringResource(R.string.challenge_summer_desc)
}

@Composable
private fun badgeTitle(type: ChallengeType): String = challengeTitle(type)
