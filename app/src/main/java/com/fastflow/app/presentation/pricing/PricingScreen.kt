package com.fastflow.app.presentation.pricing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import com.fastflow.app.R
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.presentation.theme.AccentBlue
import com.fastflow.app.presentation.theme.AccentOrange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(
    onBack: () -> Unit,
    viewModel: PricingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = LocalContext.current as Activity
    val purchaseSuccess = stringResource(R.string.pricing_purchase_success)
    val restoreSuccess = stringResource(R.string.pricing_restore_success)
    val restoreEmpty = stringResource(R.string.pricing_restore_empty)
    val billingNotReady = stringResource(R.string.pricing_billing_not_ready)
    val productUnavailable = stringResource(R.string.pricing_product_unavailable)
    val billingError = stringResource(R.string.pricing_billing_error)

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            if (event is PricingEvent.ShowMessage) {
                val msg = when (event.message) {
                    PricingMessage.PurchaseSuccess -> purchaseSuccess
                    PricingMessage.RestoreSuccess -> restoreSuccess
                    PricingMessage.RestoreEmpty -> restoreEmpty
                    PricingMessage.BillingNotReady -> billingNotReady
                    PricingMessage.ProductUnavailable -> productUnavailable
                    PricingMessage.BillingError -> billingError
                }
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.pricing_title),
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.pricing_hero_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.pricing_hero_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            CurrentPlanBadge(tier = uiState.currentTier)

            PlanCard(
                title = stringResource(R.string.pricing_plan_pro),
                badge = stringResource(R.string.pricing_most_popular),
                monthlyPrice = stringResource(R.string.pricing_pro_monthly),
                yearlyPrice = stringResource(R.string.pricing_pro_yearly),
                yearlyPerMonth = stringResource(R.string.pricing_pro_yearly_per_month),
                savings = stringResource(R.string.pricing_pro_savings),
                features = listOf(
                    stringResource(R.string.pricing_pro_feature_1),
                    stringResource(R.string.pricing_pro_feature_2),
                    stringResource(R.string.pricing_pro_feature_3),
                    stringResource(R.string.pricing_pro_feature_4)
                ),
                accentColor = AccentBlue,
                isHighlighted = true,
                isCurrent = uiState.currentTier == SubscriptionTier.PRO,
                isLoading = uiState.isLoading,
                onSubscribe = { viewModel.onSubscribePro(activity) }
            )

            PlanCard(
                title = stringResource(R.string.pricing_plan_premium),
                badge = null,
                monthlyPrice = stringResource(R.string.pricing_premium_monthly),
                yearlyPrice = stringResource(R.string.pricing_premium_yearly),
                yearlyPerMonth = stringResource(R.string.pricing_premium_yearly_per_month),
                savings = stringResource(R.string.pricing_premium_savings),
                features = listOf(
                    stringResource(R.string.pricing_premium_feature_1),
                    stringResource(R.string.pricing_premium_feature_2),
                    stringResource(R.string.pricing_premium_feature_3)
                ),
                accentColor = AccentOrange,
                isHighlighted = false,
                isCurrent = uiState.currentTier == SubscriptionTier.PREMIUM,
                isLoading = uiState.isLoading,
                onSubscribe = { viewModel.onSubscribePremium(activity) }
            )

            Text(
                text = stringResource(R.string.pricing_trial_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = stringResource(R.string.pricing_comparison_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ComparisonTable()

            TextButton(
                onClick = viewModel::onRestorePurchases,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.pricing_restore))
            }

            Text(
                text = stringResource(R.string.pricing_legal),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CurrentPlanBadge(tier: SubscriptionTier) {
    val label = when (tier) {
        SubscriptionTier.FREE -> stringResource(R.string.pricing_current_free)
        SubscriptionTier.PRO -> stringResource(R.string.pricing_current_pro)
        SubscriptionTier.PREMIUM -> stringResource(R.string.pricing_current_premium)
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    badge: String?,
    monthlyPrice: String,
    yearlyPrice: String,
    yearlyPerMonth: String,
    savings: String,
    features: List<String>,
    accentColor: androidx.compose.ui.graphics.Color,
    isHighlighted: Boolean,
    isCurrent: Boolean,
    isLoading: Boolean = false,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighlighted) 6.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                badge?.let {
                    Surface(
                        color = accentColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = monthlyPrice,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = stringResource(R.string.pricing_or_yearly, yearlyPrice),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = yearlyPerMonth,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            Text(
                text = savings,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSubscribe,
                enabled = !isCurrent && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    if (isCurrent) {
                        stringResource(R.string.pricing_current_plan)
                    } else {
                        stringResource(R.string.pricing_subscribe)
                    }
                )
            }
        }
    }
}

private data class ComparisonRow(
    val labelRes: Int,
    val free: Boolean,
    val pro: Boolean,
    val premium: Boolean
)

@Composable
private fun ComparisonTable() {
    val rows = listOf(
        ComparisonRow(R.string.pricing_row_timer, true, true, true),
        ComparisonRow(R.string.pricing_row_16_8, true, true, true),
        ComparisonRow(R.string.pricing_row_plans, false, true, true),
        ComparisonRow(R.string.pricing_row_history, false, true, true),
        ComparisonRow(R.string.pricing_row_stats, false, true, true),
        ComparisonRow(R.string.pricing_row_widget, false, true, true),
        ComparisonRow(R.string.pricing_row_reminders, true, true, true),
        ComparisonRow(R.string.pricing_row_export, false, true, true),
        ComparisonRow(R.string.pricing_row_health, false, false, true),
        ComparisonRow(R.string.pricing_row_coach, false, false, true),
        ComparisonRow(R.string.pricing_row_themes, false, true, true),
        ComparisonRow(R.string.pricing_row_ads, false, true, true)
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp)
            ) {
                Text(
                    stringResource(R.string.pricing_col_feature),
                    modifier = Modifier.weight(1.4f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    stringResource(R.string.pricing_col_free),
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    stringResource(R.string.pricing_col_pro),
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    stringResource(R.string.pricing_col_premium),
                    modifier = Modifier.weight(0.6f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Text(
                        stringResource(row.labelRes),
                        modifier = Modifier.weight(1.4f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    ComparisonCell(included = row.free, modifier = Modifier.weight(0.5f))
                    ComparisonCell(included = row.pro, modifier = Modifier.weight(0.5f))
                    ComparisonCell(included = row.premium, modifier = Modifier.weight(0.6f))
                }
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun ComparisonCell(included: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = if (included) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (included) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            },
            modifier = Modifier.size(18.dp)
        )
    }
}
