package com.fastflow.app.di

import com.fastflow.app.domain.repository.SubscriptionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BillingEntryPoint {
    fun subscriptionRepository(): SubscriptionRepository
}
