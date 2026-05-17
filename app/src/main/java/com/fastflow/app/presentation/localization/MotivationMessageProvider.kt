package com.fastflow.app.presentation.localization

import android.content.Context
import com.fastflow.app.R
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.model.UserStats
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class MotivationMessageProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getMessage(session: FastingSession?, stats: UserStats): String {
        if (session != null && session.isActive()) {
            val hours = session.getElapsedHours()
            val remainingHours = session.getRemainingTime() / 3_600_000f
            return when {
                session.status == FastingStatus.PAUSED ->
                    context.getString(R.string.motivation_paused)
                hours >= 12f ->
                    context.getString(R.string.motivation_fat_burn)
                remainingHours <= 2f -> {
                    val h = remainingHours.roundToInt().coerceAtLeast(1)
                    context.getString(R.string.motivation_almost_done, h)
                }
                hours >= 4f ->
                    context.getString(R.string.motivation_hydrate)
                else ->
                    context.getString(R.string.motivation_every_minute)
            }
        }

        return when {
            stats.currentStreak >= 7 ->
                context.getString(R.string.motivation_streak_7, stats.currentStreak)
            stats.currentStreak >= 3 ->
                context.getString(R.string.motivation_streak_3, stats.currentStreak)
            stats.totalFastsCompleted == 0 ->
                context.getString(R.string.motivation_ready_start)
            stats.weightLost > 0 ->
                context.getString(R.string.motivation_weight_lost, stats.weightLost)
            else ->
                context.getString(R.string.motivation_consistency)
        }
    }
}
