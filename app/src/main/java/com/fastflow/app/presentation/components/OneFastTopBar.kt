package com.fastflow.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fastflow.app.R

@Composable
fun OneFastTopBarLogo(
    modifier: Modifier = Modifier,
    variant: OneFastLogoVariant = OneFastLogoVariant.Full,
    width: Dp = 120.dp,
    height: Dp = 52.dp,
    contentDescription: String? = stringResource(R.string.app_name)
) {
    OneFastLogo(
        modifier = modifier,
        variant = variant,
        width = width,
        height = height,
        contentDescription = contentDescription
    )
}
