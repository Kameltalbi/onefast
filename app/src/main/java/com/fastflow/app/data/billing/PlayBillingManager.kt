package com.fastflow.app.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.fastflow.app.domain.model.SubscriptionTier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class PlayBillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tierStore: SubscriptionTierStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var productDetailsById: Map<String, ProductDetails> = emptyMap()
    private var pendingPurchaseCallback: ((Result<Unit>) -> Unit)? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    scope.launch {
                        val tier = resolveTierFromPurchases(purchases)
                        tierStore.setTier(tier)
                        purchases.forEach { acknowledgeIfNeeded(it) }
                        pendingPurchaseCallback?.invoke(Result.success(Unit))
                        pendingPurchaseCallback = null
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                pendingPurchaseCallback?.invoke(Result.failure(BillingException.Cancelled))
                pendingPurchaseCallback = null
            }
            else -> {
                pendingPurchaseCallback?.invoke(
                    Result.failure(BillingException.Error(billingResult.responseCode, billingResult.debugMessage))
                )
                pendingPurchaseCallback = null
            }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun startConnection() {
        if (billingClient.isReady) {
            _isReady.value = true
            scope.launch { queryProductsAndRestore() }
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isReady.value = true
                    scope.launch { queryProductsAndRestore() }
                }
            }

            override fun onBillingServiceDisconnected() {
                _isReady.value = false
            }
        })
    }

    suspend fun launchPurchase(activity: Activity, productId: String): Result<Unit> {
        if (!billingClient.isReady) {
            return Result.failure(BillingException.NotReady)
        }
        val details = productDetailsById[productId]
            ?: return Result.failure(BillingException.ProductNotFound(productId))

        val offerToken = details.subscriptionOfferDetails
            ?.firstOrNull()
            ?.offerToken
            ?: return Result.failure(BillingException.ProductNotFound(productId))

        return suspendCancellableCoroutine { cont ->
            pendingPurchaseCallback = { result ->
                if (cont.isActive) cont.resume(result)
            }
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(offerToken)
                .build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()
            val launchResult = billingClient.launchBillingFlow(activity, flowParams)
            if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                pendingPurchaseCallback = null
                cont.resume(
                    Result.failure(
                        BillingException.Error(launchResult.responseCode, launchResult.debugMessage)
                    )
                )
            }
        }
    }

    suspend fun restorePurchases(): Result<SubscriptionTier> {
        if (!billingClient.isReady) {
            return Result.failure(BillingException.NotReady)
        }
        val purchases = queryActivePurchases()
        val tier = resolveTierFromPurchases(purchases)
        purchases.forEach { acknowledgeIfNeeded(it) }
        tierStore.setTier(tier)
        return Result.success(tier)
    }

    private suspend fun queryProductsAndRestore() {
        queryProductDetails()
        val purchases = queryActivePurchases()
        val tier = resolveTierFromPurchases(purchases)
        tierStore.setTier(tier)
        purchases.forEach { acknowledgeIfNeeded(it) }
    }

    private suspend fun queryProductDetails() {
        val productList = BillingProductIds.ALL_SUBSCRIPTIONS.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        val result = billingClient.queryProductDetails(params)
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            productDetailsById = result.productDetailsList.orEmpty().associateBy { it.productId }
        }
    }

    private suspend fun queryActivePurchases(): List<Purchase> =
        suspendCancellableCoroutine { cont ->
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    cont.resume(purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED })
                } else {
                    cont.resume(emptyList())
                }
            }
        }

    private suspend fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        suspendCancellableCoroutine { cont ->
            billingClient.acknowledgePurchase(params) { result ->
                cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
            }
        }
    }

    private fun resolveTierFromPurchases(purchases: List<Purchase>): SubscriptionTier {
        var tier = SubscriptionTier.FREE
        purchases
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .forEach { purchase ->
                purchase.products.forEach { productId ->
                    tier = maxOf(
                        tier,
                        when {
                            productId in BillingProductIds.PREMIUM_PRODUCTS -> SubscriptionTier.PREMIUM
                            productId in BillingProductIds.PRO_PRODUCTS -> SubscriptionTier.PRO
                            else -> SubscriptionTier.FREE
                        },
                        compareBy { it.rank }
                    )
                }
            }
        return tier
    }
}

sealed class BillingException(message: String) : Exception(message) {
    data object NotReady : BillingException("Billing client not ready")
    data object Cancelled : BillingException("Purchase cancelled")
    data class ProductNotFound(val productId: String) :
        BillingException("Product not found: $productId")
    data class Error(val code: Int, val debugMessage: String?) :
        BillingException("Billing error $code: $debugMessage")
}
