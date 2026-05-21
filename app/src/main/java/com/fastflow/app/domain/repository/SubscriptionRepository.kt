package com.fastflow.app.domain.repository

import android.app.Activity
import com.fastflow.app.domain.model.SubscriptionTier
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeTier(): Flow<SubscriptionTier>
    suspend fun getTierOnce(): SubscriptionTier
    suspend fun setTier(tier: SubscriptionTier)
    suspend fun isProOrAbove(): Boolean
    suspend fun isPremium(): Boolean
    fun startBillingConnection()
    suspend fun purchaseProYearly(activity: Activity): Result<Unit>
    suspend fun purchasePremiumYearly(activity: Activity): Result<Unit>
    suspend fun restorePurchases(): Result<SubscriptionTier>
}
