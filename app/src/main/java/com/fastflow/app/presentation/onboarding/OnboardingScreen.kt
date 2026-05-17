package com.fastflow.app.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import android.app.Activity
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingExperienceLevel
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.OnboardingGoal
import com.fastflow.app.domain.onboarding.FastingPlanRecommender
import com.fastflow.app.domain.onboarding.PlanDifficulty
import com.fastflow.app.presentation.components.LanguageSelector
import com.fastflow.app.presentation.localization.LocalizedApp
import com.fastflow.app.presentation.theme.AccentBlue
import com.fastflow.app.presentation.theme.AccentOrange
import com.fastflow.app.presentation.localization.localizedName
import com.fastflow.app.presentation.localization.localizedPlanSummary
import com.fastflow.app.presentation.theme.PrimaryBlueNight

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authSoonMessage = stringResource(R.string.onboarding_auth_soon)
    val activity = LocalContext.current as Activity

    LocalizedApp(languageTag = uiState.languageTag) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PrimaryBlueNight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.step > 0) {
                OnboardingProgressBar(
                    currentStep = uiState.progressStep,
                    totalSteps = OnboardingUiState.TOTAL_STEPS,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }

            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = {
                    fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 4 } togetherWith
                        fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 }
                },
                label = "onboarding_step",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    0 -> WelcomeStep(
                        selectedLanguage = uiState.languageTag,
                        onLanguageSelect = { viewModel.selectLanguage(it, activity) },
                        onStart = { viewModel.nextStep() },
                        onGoogle = {
                            scope.launch {
                                snackbarHostState.showSnackbar(authSoonMessage)
                            }
                        },
                        onApple = {
                            scope.launch {
                                snackbarHostState.showSnackbar(authSoonMessage)
                            }
                        }
                    )
                    1 -> GoalStep(
                        selected = uiState.goal,
                        onSelect = viewModel::selectGoal,
                        onNext = { if (uiState.canAdvanceFromGoal()) viewModel.nextStep() },
                        onBack = viewModel::previousStep,
                        canContinue = uiState.canAdvanceFromGoal()
                    )
                    2 -> ProfileStep(
                        age = uiState.age,
                        heightCm = uiState.heightCm,
                        weightKg = uiState.weightKg,
                        targetWeightKg = uiState.targetWeightKg,
                        onAgeChange = viewModel::updateAge,
                        onHeightChange = viewModel::updateHeight,
                        onWeightChange = viewModel::updateWeight,
                        onTargetChange = viewModel::updateTargetWeight,
                        onNext = { if (uiState.canAdvanceFromProfile()) viewModel.nextStep() },
                        onBack = viewModel::previousStep,
                        canContinue = uiState.canAdvanceFromProfile()
                    )
                    3 -> ExperienceStep(
                        selected = uiState.experience,
                        onSelect = viewModel::selectExperience,
                        onNext = { if (uiState.canAdvanceFromLevel()) viewModel.nextStep() },
                        onBack = viewModel::previousStep,
                        canContinue = uiState.canAdvanceFromLevel()
                    )
                    4 -> RecommendationStep(
                        plan = uiState.recommendedPlan,
                        onStart = { viewModel.completeOnboarding(onComplete) },
                        onBack = viewModel::previousStep,
                        isLoading = uiState.isSaving
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun OnboardingProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.onboarding_step_label, currentStep, totalSteps),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = currentStep.toFloat() / totalSteps,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = AccentOrange,
            trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun WelcomeStep(
    selectedLanguage: String,
    onLanguageSelect: (String) -> Unit,
    onStart: () -> Unit,
    onGoogle: () -> Unit,
    onApple: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        LanguageSelector(
            selectedTag = selectedLanguage,
            onSelect = onLanguageSelect
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBlue.copy(alpha = 0.5f), PrimaryBlueNight)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⏱",
                fontSize = 52.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_slogan),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        OnboardingPrimaryButton(
            text = stringResource(R.string.onboarding_start),
            onClick = onStart
        )

        Spacer(modifier = Modifier.height(12.dp))

        OnboardingOutlinedButton(
            text = stringResource(R.string.onboarding_continue_google),
            onClick = onGoogle
        )

        Spacer(modifier = Modifier.height(10.dp))

        OnboardingOutlinedButton(
            text = stringResource(R.string.onboarding_continue_apple),
            onClick = onApple
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun GoalStep(
    selected: OnboardingGoal?,
    onSelect: (OnboardingGoal) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    canContinue: Boolean
) {
    OnboardingStepScaffold(
        title = stringResource(R.string.onboarding_goal_title),
        onBack = onBack,
        onNext = onNext,
        canContinue = canContinue
    ) {
        val goals = listOf(
            Triple(OnboardingGoal.LOSE_WEIGHT, R.string.onboarding_goal_lose_weight, Icons.Default.TrendingDown),
            Triple(OnboardingGoal.BETTER_ENERGY, R.string.onboarding_goal_energy, Icons.Default.Bolt),
            Triple(OnboardingGoal.HEALTHY_LIFESTYLE, R.string.onboarding_goal_lifestyle, Icons.Default.Favorite),
            Triple(OnboardingGoal.RAMADAN_FASTING, R.string.onboarding_goal_ramadan, Icons.Default.NightsStay)
        )
        goals.forEach { (goal, labelRes, icon) ->
            SelectionCard(
                title = stringResource(labelRes),
                icon = icon,
                selected = selected == goal,
                onClick = { onSelect(goal) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ProfileStep(
    age: Int,
    heightCm: Int,
    weightKg: Float,
    targetWeightKg: Float,
    onAgeChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    onWeightChange: (Float) -> Unit,
    onTargetChange: (Float) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    canContinue: Boolean
) {
    OnboardingStepScaffold(
        title = stringResource(R.string.onboarding_profile_title),
        subtitle = stringResource(R.string.onboarding_profile_subtitle),
        onBack = onBack,
        onNext = onNext,
        canContinue = canContinue
    ) {
        ProfileSlider(
            label = stringResource(R.string.onboarding_age),
            valueText = "$age",
            value = age.toFloat(),
            valueRange = 16f..80f,
            steps = 64,
            onValueChange = { onAgeChange(it.toInt()) }
        )
        ProfileSlider(
            label = stringResource(R.string.onboarding_height),
            valueText = stringResource(R.string.onboarding_height_value, heightCm),
            value = heightCm.toFloat(),
            valueRange = 140f..210f,
            steps = 70,
            onValueChange = { onHeightChange(it.toInt()) }
        )
        ProfileSlider(
            label = stringResource(R.string.onboarding_weight),
            valueText = stringResource(R.string.onboarding_weight_value, weightKg),
            value = weightKg,
            valueRange = 45f..150f,
            steps = 105,
            onValueChange = onWeightChange
        )
        ProfileSlider(
            label = stringResource(R.string.onboarding_target_weight),
            valueText = stringResource(R.string.onboarding_weight_value, targetWeightKg),
            value = targetWeightKg,
            valueRange = 45f..150f,
            steps = 105,
            onValueChange = onTargetChange
        )
    }
}

@Composable
private fun ExperienceStep(
    selected: FastingExperienceLevel?,
    onSelect: (FastingExperienceLevel) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    canContinue: Boolean
) {
    OnboardingStepScaffold(
        title = stringResource(R.string.onboarding_experience_title),
        onBack = onBack,
        onNext = onNext,
        canContinue = canContinue
    ) {
        val levels = listOf(
            Triple(FastingExperienceLevel.BEGINNER, R.string.onboarding_level_beginner, Icons.Default.StarOutline),
            Triple(FastingExperienceLevel.INTERMEDIATE, R.string.onboarding_level_intermediate, Icons.Default.StarHalf),
            Triple(FastingExperienceLevel.ADVANCED, R.string.onboarding_level_advanced, Icons.Default.Star)
        )
        levels.forEach { (level, labelRes, icon) ->
            SelectionCard(
                title = stringResource(labelRes),
                icon = icon,
                selected = selected == level,
                onClick = { onSelect(level) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun RecommendationStep(
    plan: FastingType,
    onStart: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    val difficulty = FastingPlanRecommender.difficulty(plan)
    val difficultyLabel = when (difficulty) {
        PlanDifficulty.EASY -> stringResource(R.string.onboarding_difficulty_easy)
        PlanDifficulty.MODERATE -> stringResource(R.string.onboarding_difficulty_moderate)
        PlanDifficulty.CHALLENGING -> stringResource(R.string.onboarding_difficulty_challenging)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        IconButton(onClick = onBack, enabled = !isLoading) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_recommendation_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = plan.localizedName(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    RecommendationRow(
                        icon = Icons.Default.Timer,
                        label = stringResource(R.string.onboarding_fasting_hours),
                        value = stringResource(R.string.onboarding_hours_format, plan.fastingHours)
                    )
                    RecommendationRow(
                        icon = Icons.Default.Restaurant,
                        label = stringResource(R.string.onboarding_eating_hours),
                        value = stringResource(R.string.onboarding_hours_format, plan.eatingHours)
                    )
                    RecommendationRow(
                        icon = Icons.Default.Speed,
                        label = stringResource(R.string.onboarding_difficulty),
                        value = difficultyLabel
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = plan.localizedPlanSummary(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }

        OnboardingPrimaryButton(
            text = stringResource(R.string.onboarding_start_journey),
            onClick = onStart,
            enabled = !isLoading,
            loading = isLoading
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun RecommendationRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun OnboardingStepScaffold(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    onNext: () -> Unit,
    canContinue: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            content = content
        )

        OnboardingPrimaryButton(
            text = stringResource(R.string.onboarding_continue),
            onClick = onNext,
            enabled = canContinue
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionCard(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                AccentBlue.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
            }
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, AccentBlue)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) AccentOrange else MaterialTheme.colorScheme.onBackground.copy(0.7f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileSlider(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AccentOrange
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = AccentOrange,
                activeTrackColor = AccentBlue
            )
        )
    }
}

@Composable
private fun OnboardingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentBlue,
            disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OnboardingOutlinedButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
        )
    }
}
