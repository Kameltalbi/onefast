package com.fastflow.app

import android.app.Application
import com.fastflow.app.di.BillingEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors

@HiltAndroidApp
class FastFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val billingEntry = EntryPointAccessors.fromApplication(this, BillingEntryPoint::class.java)
        billingEntry.subscriptionRepository().startBillingConnection()
    }
}
