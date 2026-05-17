package com.fastflow.app.presentation.localization

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingType

@StringRes
fun FastingType.labelRes(): Int = when (this) {
    FastingType.TWELVE_TWELVE -> R.string.plan_12_12
    FastingType.FOURTEEN_TEN -> R.string.plan_14_10
    FastingType.SIXTEEN_EIGHT -> R.string.plan_16_8
    FastingType.EIGHTEEN_SIX -> R.string.plan_18_6
    FastingType.TWENTY_FOUR -> R.string.plan_20_4
    FastingType.OMAD -> R.string.plan_omad
    FastingType.CUSTOM -> R.string.plan_custom
}

fun FastingType.getLabel(context: Context): String = context.getString(labelRes())

fun FastingType.formatPlanSummary(
    context: Context,
    customFastingHours: Int? = null
): String {
    val fast = resolveFastingHours(customFastingHours)
    val eat = resolveEatingHours(customFastingHours)
    return context.getString(R.string.plan_summary_format, fast, eat)
}

@Composable
@ReadOnlyComposable
fun FastingType.localizedName(): String = stringResource(labelRes())

@Composable
@ReadOnlyComposable
fun FastingType.localizedPlanSummary(customFastingHours: Int? = null): String {
    val fast = resolveFastingHours(customFastingHours)
    val eat = resolveEatingHours(customFastingHours)
    return stringResource(R.string.plan_summary_format, fast, eat)
}
