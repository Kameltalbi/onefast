package com.fastflow.app.data.meal

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealPlanParser @Inject constructor() {

    fun extractTitle(content: String, fallback: String): String {
        val line = content.lines().firstOrNull { it.startsWith("#") }
        return line?.removePrefix("#")?.trim()?.ifBlank { null } ?: fallback
    }

    fun extractShoppingList(content: String, fallback: String): String {
        val markers = listOf(
            "liste de courses",
            "liste courses",
            "shopping list"
        )
        val lower = content.lowercase()
        val index = markers.mapNotNull { marker ->
            lower.indexOf(marker).takeIf { it >= 0 }
        }.minOrNull() ?: return fallback

        return content.substring(index).trim().ifBlank { fallback }
    }
}
