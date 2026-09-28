package com.example.drundoom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    const val FOREGROUND_CHANNEL_ID = "scroll_watcher_service_channel"
    const val ALERT_CHANNEL_ID = "scroll_alert_channel_final"
    const val FOREGROUND_NOTIFICATION_ID = 1001
    const val ALERT_NOTIFICATION_ID = 1002
    const val TARGET_ALERT_NOTIFICATION_ID = 1003
    const val TARGET_WARNING_NOTIFICATION_ID = 1004

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val serviceChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Scroll Watcher (background)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps track of continuous scrolling in the background"
            }

            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Scroll Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts you when you've been scrolling too long"
                // Using the system default notification sound — no custom sound file
            }

            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(alertChannel)
        }
    }

    // The ongoing, low-priority notification Android requires while a
    // foreground service is running. Tapping it opens the app.
    fun buildForegroundNotification(context: Context): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
            .setContentTitle("DrUnDoom is watching")
            .setContentText("Tracking continuous scrolling in the background")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    // The actual "you've been scrolling too long" alert
    fun showScrollAlert(context: Context, appName: String, minutes: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("Time for a break? \uD83D\uDC40")
            .setContentText("You've been scrolling $appName for $minutes minutes straight.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(ALERT_NOTIFICATION_ID, notification)
    }

    fun showTargetReachedAlert(context: Context, targetMinutes: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 2, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("\u26A0\uFE0F Daily target reached")
            .setContentText("You've hit your $targetMinutes-minute screen time goal for today. Time to stop.")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(TARGET_ALERT_NOTIFICATION_ID, notification)
    }

    fun showTargetWarningAlert(context: Context, minutesRemaining: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 3, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("\u26A0\uFE0F Approaching your daily target")
            .setContentText("Only $minutesRemaining minutes left before you hit your goal today.")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(TARGET_WARNING_NOTIFICATION_ID, notification)
    }
}