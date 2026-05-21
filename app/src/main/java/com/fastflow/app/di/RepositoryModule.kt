package com.fastflow.app.di

import com.fastflow.app.data.repository.ChallengeRepositoryImpl
import com.fastflow.app.data.repository.CommunityRepositoryImpl
import com.fastflow.app.data.repository.HealthSyncRepositoryImpl
import com.fastflow.app.data.repository.MealPlanRepositoryImpl
import com.fastflow.app.data.repository.RamadanRepositoryImpl
import com.fastflow.app.data.repository.CoachRepositoryImpl
import com.fastflow.app.data.repository.FastingRepositoryImpl
import com.fastflow.app.data.repository.HydrationRepositoryImpl
import com.fastflow.app.data.repository.SubscriptionRepositoryImpl
import com.fastflow.app.data.repository.StatsRepositoryImpl
import com.fastflow.app.data.repository.WeightRepositoryImpl
import com.fastflow.app.domain.repository.ChallengeRepository
import com.fastflow.app.domain.repository.CommunityRepository
import com.fastflow.app.domain.repository.HealthSyncRepository
import com.fastflow.app.domain.repository.MealPlanRepository
import com.fastflow.app.domain.repository.RamadanRepository
import com.fastflow.app.domain.repository.CoachRepository
import com.fastflow.app.domain.repository.FastingRepository
import com.fastflow.app.domain.repository.HydrationRepository
import com.fastflow.app.domain.repository.SubscriptionRepository
import com.fastflow.app.domain.repository.StatsRepository
import com.fastflow.app.domain.repository.WeightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFastingRepository(
        impl: FastingRepositoryImpl
    ): FastingRepository

    @Binds
    @Singleton
    abstract fun bindWeightRepository(
        impl: WeightRepositoryImpl
    ): WeightRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        impl: StatsRepositoryImpl
    ): StatsRepository

    @Binds
    @Singleton
    abstract fun bindCoachRepository(
        impl: CoachRepositoryImpl
    ): CoachRepository

    @Binds
    @Singleton
    abstract fun bindChallengeRepository(
        impl: ChallengeRepositoryImpl
    ): ChallengeRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(
        impl: CommunityRepositoryImpl
    ): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindMealPlanRepository(
        impl: MealPlanRepositoryImpl
    ): MealPlanRepository

    @Binds
    @Singleton
    abstract fun bindHealthSyncRepository(
        impl: HealthSyncRepositoryImpl
    ): HealthSyncRepository

    @Binds
    @Singleton
    abstract fun bindRamadanRepository(
        impl: RamadanRepositoryImpl
    ): RamadanRepository

    @Binds
    @Singleton
    abstract fun bindHydrationRepository(
        impl: HydrationRepositoryImpl
    ): HydrationRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository
}
