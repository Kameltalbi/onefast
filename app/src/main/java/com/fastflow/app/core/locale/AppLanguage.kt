package com.fastflow.app.core.locale

/**
 * Langues supportées par les ressources `values-*` du projet.
 * [nativeLabel] est affiché dans le sélecteur (nom dans la langue elle-même).
 */
data class AppLanguage(
    val tag: String,
    val nativeLabel: String
)

object AppLanguages {
    val all: List<AppLanguage> = listOf(
        AppLanguage(AppLocaleManager.LANGUAGE_FRENCH, "Français"),
        AppLanguage(AppLocaleManager.LANGUAGE_ENGLISH, "English"),
        AppLanguage(AppLocaleManager.LANGUAGE_SPANISH, "Español"),
        AppLanguage(AppLocaleManager.LANGUAGE_GERMAN, "Deutsch"),
        AppLanguage(AppLocaleManager.LANGUAGE_PORTUGUESE, "Português"),
        AppLanguage(AppLocaleManager.LANGUAGE_ARABIC, "العربية"),
        AppLanguage(AppLocaleManager.LANGUAGE_TURKISH, "Türkçe")
    )

    fun findByTag(tag: String): AppLanguage? = all.find { it.tag == tag }
}
