package com.fastflow.app.presentation.meal

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.meal.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealPlanScreen(
    onBack: () -> Unit,
    viewModel: MealPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.meal_plan_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = uiState.selectedTab) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = { Text(stringResource(R.string.meal_tab_generate)) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = { Text(stringResource(R.string.meal_tab_result)) },
                    enabled = uiState.selectedPlan != null
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setTab(2) },
                    text = { Text(stringResource(R.string.meal_tab_favorites)) }
                )
            }

            when (uiState.selectedTab) {
                0 -> GenerateTab(uiState, viewModel)
                1 -> ResultTab(
                    plan = uiState.selectedPlan,
                    dateFormat = dateFormat,
                    onFavorite = { uiState.selectedPlan?.id?.let(viewModel::toggleFavorite) },
                    onDelete = { uiState.selectedPlan?.id?.let(viewModel::deletePlan) },
                    onShare = { list ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.meal_shopping_share_subject))
                            putExtra(Intent.EXTRA_TEXT, list)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    }
                )
                2 -> FavoritesTab(
                    favorites = uiState.favorites,
                    history = uiState.history,
                    dateFormat = dateFormat,
                    onSelect = { viewModel.selectPlan(it); viewModel.setTab(1) },
                    onFavorite = viewModel::toggleFavorite,
                    onDelete = viewModel::deletePlan
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun GenerateTab(uiState: MealPlanUiState, viewModel: MealPlanViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!uiState.isPremium) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.meal_premium_hint),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        uiState.error?.let { error ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(error, modifier = Modifier.padding(12.dp))
            }
        }

        OutlinedTextField(
            value = uiState.caloriesText,
            onValueChange = viewModel::updateCalories,
            label = { Text(stringResource(R.string.meal_calories)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.cuisine,
            onValueChange = viewModel::updateCuisine,
            label = { Text(stringResource(R.string.meal_cuisine)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(stringResource(R.string.meal_goal_label), fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MealGoal.entries.forEach { goal ->
                FilterChip(
                    selected = uiState.goal == goal,
                    onClick = { viewModel.setGoal(goal) },
                    label = { Text(goalLabel(goal)) }
                )
            }
        }

        Text(stringResource(R.string.meal_diet_label), fontWeight = FontWeight.Medium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DietPreference.entries.forEach { diet ->
                FilterChip(
                    selected = uiState.diet == diet,
                    onClick = { viewModel.setDiet(diet) },
                    label = { Text(dietLabel(diet)) }
                )
            }
        }

        Text(stringResource(R.string.meal_budget_label), fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BudgetLevel.entries.forEach { budget ->
                FilterChip(
                    selected = uiState.budget == budget,
                    onClick = { viewModel.setBudget(budget) },
                    label = { Text(budgetLabel(budget)) }
                )
            }
        }

        Text(stringResource(R.string.meal_days_label, uiState.days), fontWeight = FontWeight.Medium)
        Slider(
            value = uiState.days.toFloat(),
            onValueChange = { viewModel.setDays(it.toInt()) },
            valueRange = 1f..7f,
            steps = 5
        )

        Button(
            onClick = { viewModel.generate(forceLocal = false) },
            enabled = !uiState.isGenerating,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Default.Restaurant, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                if (uiState.isPremium) stringResource(R.string.meal_generate_ai)
                else stringResource(R.string.meal_generate)
            )
        }

        if (!uiState.isPremium) {
            OutlinedButton(
                onClick = { viewModel.generate(forceLocal = true) },
                enabled = !uiState.isGenerating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.meal_generate_local))
            }
        }
    }
}

@Composable
private fun ResultTab(
    plan: MealPlan?,
    dateFormat: SimpleDateFormat,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onShare: (String) -> Unit
) {
    if (plan == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.meal_no_plan), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plan.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    dateFormat.format(Date(plan.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    if (plan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(plan.planContent, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.meal_shopping_list), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(plan.shoppingList, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onShare(plan.shoppingList) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.meal_export_shopping))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesTab(
    favorites: List<MealPlan>,
    history: List<MealPlan>,
    dateFormat: SimpleDateFormat,
    onSelect: (MealPlan) -> Unit,
    onFavorite: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (favorites.isNotEmpty()) {
            item {
                Text(stringResource(R.string.meal_tab_favorites), fontWeight = FontWeight.Bold)
            }
            items(favorites, key = { it.id }) { plan ->
                PlanListItem(plan, dateFormat, onSelect, onFavorite, onDelete)
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.meal_history), fontWeight = FontWeight.Bold)
        }
        if (history.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.meal_history_empty),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            items(history, key = { it.id }) { plan ->
                PlanListItem(plan, dateFormat, onSelect, onFavorite, onDelete)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanListItem(
    plan: MealPlan,
    dateFormat: SimpleDateFormat,
    onSelect: (MealPlan) -> Unit,
    onFavorite: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(onClick = { onSelect(plan) }, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plan.title, fontWeight = FontWeight.Medium)
                Text(
                    "${plan.request.caloriesTarget} kcal · ${dateFormat.format(Date(plan.createdAt))}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            IconButton(onClick = { onFavorite(plan.id) }) {
                Icon(
                    if (plan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }
            IconButton(onClick = { onDelete(plan.id) }) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
private fun goalLabel(goal: MealGoal): String = when (goal) {
    MealGoal.WEIGHT_LOSS -> stringResource(R.string.meal_goal_loss)
    MealGoal.MAINTENANCE -> stringResource(R.string.meal_goal_maintain)
    MealGoal.MUSCLE_GAIN -> stringResource(R.string.meal_goal_muscle)
}

@Composable
private fun dietLabel(diet: DietPreference): String = when (diet) {
    DietPreference.STANDARD -> stringResource(R.string.meal_diet_standard)
    DietPreference.HALAL -> stringResource(R.string.meal_diet_halal)
    DietPreference.VEGETARIAN -> stringResource(R.string.meal_diet_vegetarian)
    DietPreference.VEGAN -> stringResource(R.string.meal_diet_vegan)
    DietPreference.KETO -> stringResource(R.string.meal_diet_keto)
}

@Composable
private fun budgetLabel(budget: BudgetLevel): String = when (budget) {
    BudgetLevel.LOW -> stringResource(R.string.meal_budget_low)
    BudgetLevel.MEDIUM -> stringResource(R.string.meal_budget_medium)
    BudgetLevel.HIGH -> stringResource(R.string.meal_budget_high)
}
