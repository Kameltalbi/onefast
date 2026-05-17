package com.fastflow.app.domain.util

import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.model.UserStats

object MotivationProvider {

    fun getMessage(
        session: FastingSession?,
        stats: UserStats
    ): String {
        if (session != null && session.isActive()) {
            val hours = session.getElapsedHours()
            val remainingHours = session.getRemainingTime() / 3_600_000f
            return when {
                session.status == FastingStatus.PAUSED ->
                    "Prenez une pause, puis reprenez quand vous êtes prêt."
                hours >= 12f ->
                    "Votre corps entre en phase de combustion des graisses. Continuez !"
                remainingHours <= 2f ->
                    "Plus que ${remainingHours.toInt().coerceAtLeast(1)} h — vous y êtes presque !"
                hours >= 4f ->
                    "Hydratez-vous régulièrement. Vous progressez très bien."
                else ->
                    "Chaque minute compte. Vous construisez une habitude solide."
            }
        }

        return when {
            stats.currentStreak >= 7 ->
                "Bravo ! ${stats.currentStreak} jours consécutifs. Vous êtes une machine."
            stats.currentStreak >= 3 ->
                "Série de ${stats.currentStreak} jours — la régularité paie toujours."
            stats.totalFastsCompleted == 0 ->
                "Prêt à commencer ? Un seul jeûne peut tout changer."
            stats.weightLost > 0 ->
                "Vous avez perdu ${String.format("%.1f", stats.weightLost)} kg. Continuez ainsi !"
            else ->
                "La constance bat la perfection. Démarrez votre prochain jeûne."
        }
    }

    fun estimateCaloriesBurned(session: FastingSession?): Int {
        if (session == null || !session.isActive()) return 0
        return (session.getElapsedHours() * 55f).toInt()
    }
}
