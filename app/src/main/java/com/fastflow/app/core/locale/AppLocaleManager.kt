package com.fastflow.app.core.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object AppLocaleManager {

    const val LANGUAGE_FRENCH = "fr"
    const val LANGUAGE_ENGLISH = "en"

    fun supportedTags(): List<String> = listOf(LANGUAGE_FRENCH, LANGUAGE_ENGLISH)

    fun defaultLanguageTag(): String {
        val system = Locale.getDefault().language.lowercase(Locale.ROOT)
        return if (system == LANGUAGE_FRENCH) LANGUAGE_FRENCH else LANGUAGE_ENGLISH
    }

    fun apply(languageTag: String) {
        val tag = if (languageTag in supportedTags()) languageTag else defaultLanguageTag()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
