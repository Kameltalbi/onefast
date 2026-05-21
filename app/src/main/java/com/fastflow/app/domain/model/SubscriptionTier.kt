package com.fastflow.app.domain.model

enum class SubscriptionTier(val rank: Int) {
    FREE(0),
    PRO(1),
    PREMIUM(2);

    fun hasAtLeast(required: SubscriptionTier): Boolean = rank >= required.rank

    companion object {
        fun fromName(name: String?): SubscriptionTier =
            entries.find { it.name == name } ?: FREE
    }
}

enum class SubscriptionFeature {
    BASIC_TIMER,
    PLAN_16_8,
    MULTIPLE_PLANS,
    UNLIMITED_HISTORY,
    ADVANCED_STATS,
    WIDGET_LOCKSCREEN,
    SMART_REMINDERS,
    DATA_EXPORT,
    HEALTH_SYNC,
    ADVANCED_COACHING,
    UI_THEMES,
    AD_FREE
}

object SubscriptionCapabilities {
    private val requirements: Map<SubscriptionFeature, SubscriptionTier> = mapOf(
        SubscriptionFeature.BASIC_TIMER to SubscriptionTier.FREE,
        SubscriptionFeature.PLAN_16_8 to SubscriptionTier.FREE,
        SubscriptionFeature.SMART_REMINDERS to SubscriptionTier.FREE,
        SubscriptionFeature.MULTIPLE_PLANS to SubscriptionTier.PRO,
        SubscriptionFeature.UNLIMITED_HISTORY to SubscriptionTier.PRO,
        SubscriptionFeature.ADVANCED_STATS to SubscriptionTier.PRO,
        SubscriptionFeature.WIDGET_LOCKSCREEN to SubscriptionTier.PRO,
        SubscriptionFeature.DATA_EXPORT to SubscriptionTier.PRO,
        SubscriptionFeature.UI_THEMES to SubscriptionTier.PRO,
        SubscriptionFeature.AD_FREE to SubscriptionTier.PRO,
        SubscriptionFeature.HEALTH_SYNC to SubscriptionTier.PREMIUM,
        SubscriptionFeature.ADVANCED_COACHING to SubscriptionTier.PREMIUM
    )

    fun requiredTier(feature: SubscriptionFeature): SubscriptionTier =
        requirements[feature] ?: SubscriptionTier.PREMIUM

    fun hasAccess(tier: SubscriptionTier, feature: SubscriptionFeature): Boolean =
        tier.hasAtLeast(requiredTier(feature))
}
