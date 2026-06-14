package com.fastflow.app.data.billing

import com.fastflow.app.BuildConfig
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.SubscriptionTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionTierStore @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    fun observeTier(): Flow<SubscriptionTier> =
        if (BuildConfig.FORCE_PRO) preferencesManager.subscriptionTier.map { SubscriptionTier.PRO }
        else preferencesManager.subscriptionTier

    suspend fun getTierOnce(): SubscriptionTier =
        if (BuildConfig.FORCE_PRO) SubscriptionTier.PRO
        else preferencesManager.getSubscriptionTierOnce()

    suspend fun setTier(tier: SubscriptionTier) {
        preferencesManager.setSubscriptionTier(tier)
    }
}
