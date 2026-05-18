package com.fastflow.app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fastflow.app.R

enum class OneFastLogoVariant {
    Full,
    Icon
}

@Composable
fun OneFastLogo(
    modifier: Modifier = Modifier,
    variant: OneFastLogoVariant = OneFastLogoVariant.Full,
    width: Dp? = null,
    height: Dp? = null,
    contentDescription: String? = stringResource(R.string.app_name)
) {
    @DrawableRes val drawable = when (variant) {
        OneFastLogoVariant.Full -> R.drawable.logo_onefast
        OneFastLogoVariant.Icon -> R.drawable.logo_onefast_icon
    }
    val defaultWidth = when (variant) {
        OneFastLogoVariant.Full -> 200.dp
        OneFastLogoVariant.Icon -> 72.dp
    }
    val defaultHeight = when (variant) {
        OneFastLogoVariant.Full -> 88.dp
        OneFastLogoVariant.Icon -> 72.dp
    }
    Image(
        painter = painterResource(drawable),
        contentDescription = contentDescription,
        modifier = modifier
            .then(
                Modifier
                    .width(width ?: defaultWidth)
                    .height(height ?: defaultHeight)
            ),
        contentScale = ContentScale.Fit
    )
}
