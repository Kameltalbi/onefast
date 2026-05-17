package com.fastflow.app.core.locale

import android.content.Context
import android.content.ContextWrapper
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleContextWrapper {

    fun wrap(context: Context, languageTag: String): Context {
        val tag = languageTag.takeIf { it in AppLocaleManager.supportedTags() }
            ?: AppLocaleManager.defaultLanguageTag()
        val locale = Locale.forLanguageTag(tag)
        val localeList = LocaleListCompat.forLanguageTags(tag)
        val config = context.resources.configuration
        config.setLocales(LocaleList(locale))
        ConfigurationCompat.setLocales(config, localeList)
        return context.createConfigurationContext(config)
    }
}

class LocaleContextWrapperContext(
    base: Context,
    languageTag: String
) : ContextWrapper(LocaleContextWrapper.wrap(base, languageTag))
