package com.fastflow.app.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingExperienceLevel
import com.fastflow.app.domain.model.OnboardingGoal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    onOpenPricing: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    val canSave = remember(uiState) {
        val weight = uiState.currentWeightKg.toFloatOrNull()
        val target = uiState.targetWeightKg.toFloatOrNull()
        uiState.displayName.isNotBlank() &&
            uiState.age.toIntOrNull() in 16..99 &&
            uiState.heightCm.toIntOrNull() in 100..250 &&
            weight != null && weight in 40f..250f &&
            target != null && target in 40f..250f &&
            uiState.goal != null &&
            uiState.experience != null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.nav_profile),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (canSave && !uiState.isSaving) viewModel.saveProfile() },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileAvatar(name = uiState.displayName)

            Card(
                onClick = onOpenPricing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.pricing_profile_card),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stringResource(R.string.pricing_profile_card_desc),
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

            ProfileSectionTitle(stringResource(R.string.profile_section_personal))
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = viewModel::updateDisplayName,
                label = { Text(stringResource(R.string.profile_display_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = {
                    Text(
                        "${stringResource(R.string.profile_email)} (${stringResource(R.string.profile_email_hint)})"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ProfileSectionTitle(stringResource(R.string.profile_section_body))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.age,
                    onValueChange = viewModel::updateAge,
                    label = { Text(stringResource(R.string.onboarding_age)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.heightCm,
                    onValueChange = viewModel::updateHeight,
                    label = { Text(stringResource(R.string.onboarding_height)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.currentWeightKg,
                    onValueChange = viewModel::updateCurrentWeight,
                    label = { Text(stringResource(R.string.onboarding_weight)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.targetWeightKg,
                    onValueChange = viewModel::updateTargetWeight,
                    label = { Text(stringResource(R.string.onboarding_target_weight)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            ProfileSectionTitle(stringResource(R.string.profile_section_goals))
            Text(
                stringResource(R.string.profile_main_goal),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OnboardingGoal.entries.forEach { goal ->
                    val selected = uiState.goal == goal
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.selectGoal(goal) },
                        label = { Text(goalLabel(goal)) }
                    )
                }
            }
            Text(
                stringResource(R.string.profile_experience),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FastingExperienceLevel.entries.forEach { level ->
                    val selected = uiState.experience == level
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.selectExperience(level) },
                        label = { Text(experienceLabel(level)) }
                    )
                }
            }

            ProfileSectionTitle(stringResource(R.string.profile_section_stats))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.profile_stat_streak),
                    value = "${uiState.currentStreak} ${stringResource(R.string.days_unit)}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.profile_stat_fasts),
                    value = uiState.totalFastsCompleted.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun ProfileAvatar(name: String) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun goalLabel(goal: OnboardingGoal): String = when (goal) {
    OnboardingGoal.LOSE_WEIGHT -> stringResource(R.string.onboarding_goal_lose_weight)
    OnboardingGoal.BETTER_ENERGY -> stringResource(R.string.onboarding_goal_energy)
    OnboardingGoal.HEALTHY_LIFESTYLE -> stringResource(R.string.onboarding_goal_lifestyle)
    OnboardingGoal.RAMADAN_FASTING -> stringResource(R.string.onboarding_goal_ramadan)
}

@Composable
private fun experienceLabel(level: FastingExperienceLevel): String = when (level) {
    FastingExperienceLevel.BEGINNER -> stringResource(R.string.onboarding_level_beginner)
    FastingExperienceLevel.INTERMEDIATE -> stringResource(R.string.onboarding_level_intermediate)
    FastingExperienceLevel.ADVANCED -> stringResource(R.string.onboarding_level_advanced)
}
