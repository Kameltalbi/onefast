package com.fastflow.app.presentation.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.fastflow.app.core.locale.LocaleContextWrapper

@Composable
fun LocalizedApp(
    languageTag: String,
    content: @Composable () -> Unit
) {
    val baseContext = LocalContext.current
    val configuration = androidx.compose.runtime.remember(languageTag, baseContext) {
        LocaleContextWrapper.wrap(baseContext, languageTag).resources.configuration
    }
    // Keep LocalContext as the Activity — replacing it breaks rememberLauncherForActivityResult.
    CompositionLocalProvider(LocalConfiguration provides configuration) {
        content()
    }
}
