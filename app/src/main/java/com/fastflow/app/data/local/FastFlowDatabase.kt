package com.fastflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fastflow.app.data.local.dao.ChatMessageDao
import com.fastflow.app.data.local.dao.CommunityPostDao
import com.fastflow.app.data.local.dao.EarnedBadgeDao
import com.fastflow.app.data.local.dao.MealPlanDao
import com.fastflow.app.data.local.dao.FastingSessionDao
import com.fastflow.app.data.local.dao.UserChallengeDao
import com.fastflow.app.data.local.dao.WaterEntryDao
import com.fastflow.app.data.local.dao.WeightEntryDao
import com.fastflow.app.data.local.entity.ChatMessageEntity
import com.fastflow.app.data.local.entity.CommunityPostEntity
import com.fastflow.app.data.local.entity.EarnedBadgeEntity
import com.fastflow.app.data.local.entity.MealPlanEntity
import com.fastflow.app.data.local.entity.FastingSessionEntity
import com.fastflow.app.data.local.entity.UserChallengeEntity
import com.fastflow.app.data.local.entity.WaterEntryEntity
import com.fastflow.app.data.local.entity.WeightEntryEntity

@Database(
    entities = [
        FastingSessionEntity::class,
        WeightEntryEntity::class,
        ChatMessageEntity::class,
        UserChallengeEntity::class,
        EarnedBadgeEntity::class,
        CommunityPostEntity::class,
        MealPlanEntity::class,
        WaterEntryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class FastFlowDatabase : RoomDatabase() {
    abstract fun fastingSessionDao(): FastingSessionDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun userChallengeDao(): UserChallengeDao
    abstract fun earnedBadgeDao(): EarnedBadgeDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun waterEntryDao(): WaterEntryDao

    companion object {
        const val DATABASE_NAME = "fastflow_database"
    }
}
