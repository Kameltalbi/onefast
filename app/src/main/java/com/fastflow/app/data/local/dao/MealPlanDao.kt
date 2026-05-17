package com.fastflow.app.data.local.dao

import androidx.room.*
import com.fastflow.app.data.local.entity.MealPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Insert
    suspend fun insert(entity: MealPlanEntity): Long

    @Update
    suspend fun update(entity: MealPlanEntity)

    @Delete
    suspend fun delete(entity: MealPlanEntity)

    @Query("SELECT * FROM meal_plans ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM meal_plans WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getById(id: Int): MealPlanEntity?
}
