package com.fastflow.app.di

import android.content.Context
import androidx.room.Room
import com.fastflow.app.data.local.FastFlowDatabase
import com.fastflow.app.data.local.dao.ChatMessageDao
import com.fastflow.app.data.local.dao.CommunityPostDao
import com.fastflow.app.data.local.dao.EarnedBadgeDao
import com.fastflow.app.data.local.dao.MealPlanDao
import com.fastflow.app.data.local.dao.FastingSessionDao
import com.fastflow.app.data.local.dao.UserChallengeDao
import com.fastflow.app.data.local.dao.WeightEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FastFlowDatabase {
        return Room.databaseBuilder(
            context,
            FastFlowDatabase::class.java,
            FastFlowDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFastingSessionDao(database: FastFlowDatabase): FastingSessionDao {
        return database.fastingSessionDao()
    }

    @Provides
    @Singleton
    fun provideWeightEntryDao(database: FastFlowDatabase): WeightEntryDao {
        return database.weightEntryDao()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(database: FastFlowDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideUserChallengeDao(database: FastFlowDatabase): UserChallengeDao {
        return database.userChallengeDao()
    }

    @Provides
    @Singleton
    fun provideEarnedBadgeDao(database: FastFlowDatabase): EarnedBadgeDao {
        return database.earnedBadgeDao()
    }

    @Provides
    @Singleton
    fun provideCommunityPostDao(database: FastFlowDatabase): CommunityPostDao {
        return database.communityPostDao()
    }

    @Provides
    @Singleton
    fun provideMealPlanDao(database: FastFlowDatabase): MealPlanDao {
        return database.mealPlanDao()
    }
}
