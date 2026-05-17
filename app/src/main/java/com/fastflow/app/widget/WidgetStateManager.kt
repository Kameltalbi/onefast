package com.fastflow.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun update(session: FastingSession?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        if (session != null && session.isActive()) {
            val remaining = session.getRemainingTime()
            val hours = TimeUnit.MILLISECONDS.toHours(remaining)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining) % 60
            editor.putBoolean(KEY_ACTIVE, true)
            editor.putString(KEY_STATUS, if (session.status == FastingStatus.FASTING) "Jeûne" else "Pause")
            editor.putString(KEY_TIME, String.format("%02d:%02d", hours, minutes))
            editor.putInt(KEY_PROGRESS, (session.getProgress() * 100).toInt())
            editor.putString(KEY_PLAN, session.fastingType.displayName)
        } else {
            editor.putBoolean(KEY_ACTIVE, false)
            editor.putString(KEY_STATUS, "Inactif")
            editor.putString(KEY_TIME, "--:--")
            editor.putInt(KEY_PROGRESS, 0)
            editor.putString(KEY_PLAN, "Appuyez pour démarrer")
        }
        editor.apply()

        val intent = Intent(context, FastingWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, FastingWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    companion object {
        const val PREFS_NAME = "fasting_widget"
        const val KEY_ACTIVE = "active"
        const val KEY_STATUS = "status"
        const val KEY_TIME = "time"
        const val KEY_PROGRESS = "progress"
        const val KEY_PLAN = "plan"
    }
}
