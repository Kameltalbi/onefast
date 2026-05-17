package com.fastflow.app.domain.model

enum class FastingType(
    val displayName: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val isPremium: Boolean = false
) {
    TWELVE_TWELVE("12:12 Débutant", 12, 12, false),
    FOURTEEN_TEN("14:10 Léger", 14, 10, false),
    SIXTEEN_EIGHT("16:8 Intermittent", 16, 8, false),
    EIGHTEEN_SIX("18:6 Avancé", 18, 6, false),
    TWENTY_FOUR("20:4 Warrior", 20, 4, false),
    OMAD("OMAD", 23, 1, false),
    CUSTOM("Personnalisé", 0, 0, false);

    val totalHours: Int
        get() = if (this == CUSTOM) 24 else fastingHours + eatingHours

    fun resolveFastingHours(customFastingHours: Int? = null): Int {
        return when (this) {
            CUSTOM -> customFastingHours?.coerceIn(1, 23) ?: 16
            else -> fastingHours
        }
    }

    fun resolveEatingHours(customFastingHours: Int? = null): Int {
        return when (this) {
            CUSTOM -> (24 - resolveFastingHours(customFastingHours)).coerceAtLeast(1)
            else -> eatingHours
        }
    }

    fun getFastingDurationMillis(customFastingHours: Int? = null): Long =
        resolveFastingHours(customFastingHours) * 60L * 60L * 1000L

    fun getEatingDurationMillis(customFastingHours: Int? = null): Long =
        resolveEatingHours(customFastingHours) * 60L * 60L * 1000L

    fun formatPlanSummary(customFastingHours: Int? = null): String {
        val fast = resolveFastingHours(customFastingHours)
        val eat = resolveEatingHours(customFastingHours)
        return "${fast}h jeûne / ${eat}h repas"
    }

    companion object {
        val selectablePlans: List<FastingType> = entries.filter { !it.isPremium }

        /** Plans proposés dans la V1 produit */
        val v1Plans: List<FastingType> = listOf(
            SIXTEEN_EIGHT,
            EIGHTEEN_SIX,
            OMAD,
            CUSTOM
        )
    }
}
