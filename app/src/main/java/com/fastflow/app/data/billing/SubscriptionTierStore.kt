package com.fastflow.app.data.billing

import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.domain.model.SubscriptionTier
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionTierStore @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    fun observeTier(): Flow<SubscriptionTier> = preferencesManager.subscriptionTier

    suspend fun getTierOnce(): SubscriptionTier = preferencesManager.getSubscriptionTierOnce()

    suspend fun setTier(tier: SubscriptionTier) {
        preferencesManager.setSubscriptionTier(tier)
    }
}
