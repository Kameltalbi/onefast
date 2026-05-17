package com.fastflow.app.core.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object AppLocaleManager {

    const val LANGUAGE_FRENCH = "fr"
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_SPANISH = "es"
    const val LANGUAGE_GERMAN = "de"
    const val LANGUAGE_PORTUGUESE = "pt"
    const val LANGUAGE_ARABIC = "ar"
    const val LANGUAGE_TURKISH = "tr"

    @Volatile
    var currentLanguageTag: String = LANGUAGE_ENGLISH
        private set

    fun supportedTags(): List<String> = AppLanguages.all.map { it.tag }

    fun defaultLanguageTag(): String {
        val system = Locale.getDefault().language.lowercase(Locale.ROOT)
        return if (system in supportedTags()) system else LANGUAGE_ENGLISH
    }

    fun apply(languageTag: String) {
        val tag = languageTag.takeIf { it in supportedTags() } ?: defaultLanguageTag()
        currentLanguageTag = tag
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
