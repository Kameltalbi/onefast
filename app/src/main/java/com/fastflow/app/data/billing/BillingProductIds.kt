package com.fastflow.app.data.billing

object BillingProductIds {
    const val PRO_MONTHLY = "onefast_pro_monthly"
    const val PRO_YEARLY = "onefast_pro_yearly"

    val PRO_PRODUCTS = setOf(PRO_MONTHLY, PRO_YEARLY)
    val ALL_SUBSCRIPTIONS = PRO_PRODUCTS
}
