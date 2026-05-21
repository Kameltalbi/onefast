package com.fastflow.app.data.billing

object BillingProductIds {
    const val PRO_MONTHLY = "onefast_pro_monthly"
    const val PRO_YEARLY = "onefast_pro_yearly"
    const val PREMIUM_MONTHLY = "onefast_premium_monthly"
    const val PREMIUM_YEARLY = "onefast_premium_yearly"

    val PRO_PRODUCTS = setOf(PRO_MONTHLY, PRO_YEARLY)
    val PREMIUM_PRODUCTS = setOf(PREMIUM_MONTHLY, PREMIUM_YEARLY)
    val ALL_SUBSCRIPTIONS = PRO_PRODUCTS + PREMIUM_PRODUCTS
}
