package com.fastflow.app.data.repository

import android.app.Activity
import com.fastflow.app.data.billing.BillingProductIds
import com.fastflow.app.data.billing.PlayBillingManager
import com.fastflow.app.data.billing.SubscriptionTierStore
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val tierStore: SubscriptionTierStore,
    private val billingManager: PlayBillingManager
) : SubscriptionRepository {

    override fun observeTier(): Flow<SubscriptionTier> = tierStore.observeTier()

    override suspend fun getTierOnce(): SubscriptionTier = tierStore.getTierOnce()

    override suspend fun setTier(tier: SubscriptionTier) {
        tierStore.setTier(tier)
    }

    override suspend fun isProOrAbove(): Boolean =
        getTierOnce().hasAtLeast(SubscriptionTier.PRO)

    override suspend fun isPremium(): Boolean =
        getTierOnce().hasAtLeast(SubscriptionTier.PREMIUM)

    override fun startBillingConnection() {
        billingManager.startConnection()
    }

    override suspend fun purchaseProYearly(activity: Activity): Result<Unit> =
        billingManager.launchPurchase(activity, BillingProductIds.PRO_YEARLY)

    override suspend fun purchasePremiumYearly(activity: Activity): Result<Unit> =
        billingManager.launchPurchase(activity, BillingProductIds.PREMIUM_YEARLY)

    override suspend fun restorePurchases(): Result<SubscriptionTier> =
        billingManager.restorePurchases()
}
