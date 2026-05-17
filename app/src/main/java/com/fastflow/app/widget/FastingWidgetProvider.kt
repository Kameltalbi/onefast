package com.fastflow.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.fastflow.app.R
import com.fastflow.app.presentation.MainActivity

class FastingWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences(WidgetStateManager.PREFS_NAME, Context.MODE_PRIVATE)
        val status = prefs.getString(WidgetStateManager.KEY_STATUS, "Inactif") ?: "Inactif"
        val time = prefs.getString(WidgetStateManager.KEY_TIME, "--:--") ?: "--:--"
        val progress = prefs.getInt(WidgetStateManager.KEY_PROGRESS, 0)
        val plan = prefs.getString(WidgetStateManager.KEY_PLAN, "OneFast") ?: "OneFast"

        val views = RemoteViews(context.packageName, R.layout.widget_fasting).apply {
            setTextViewText(R.id.widget_status, status)
            setTextViewText(R.id.widget_time, time)
            setTextViewText(R.id.widget_progress, "$progress%")
            setTextViewText(R.id.widget_plan, plan)
            setProgressBar(R.id.widget_progress_bar, 100, progress, false)
        }

        val launchIntent = android.content.Intent(context, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
