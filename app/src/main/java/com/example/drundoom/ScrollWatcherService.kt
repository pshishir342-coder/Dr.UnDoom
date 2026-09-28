package com.example.drundoom

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class ScrollWatcherService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastCheckedTime = 0L
    private var currentPackage: String? = null
    private var sessionStartTime = 0L
    private var alreadyNotified = false

    private val checkIntervalMs = 30_000L      // how often we poll
    private val alertThresholdMs = 15 * 60 * 1000L // 15 minutes continuous

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            checkDailyTarget()
            handler.postDelayed(this, checkIntervalMs)
        }
    }

    private fun checkDailyTarget() {
        if (!PrefsHelper.getTargetEnabled(this)) return

        val target = PrefsHelper.getTargetMinutes(this)
        val today = UsageStatsHelper.getTodayUsageMinutes(this)
        val todayDate = PrefsHelper.todayDateString()
        val minutesRemaining = target - today

        // Warn once, 15 minutes (or less) before the target is actually hit
        val alreadyWarnedToday = PrefsHelper.getTargetWarningNotifiedDate(this) == todayDate
        if (minutesRemaining in 0..15 && !alreadyWarnedToday) {
            NotificationHelper.showTargetWarningAlert(this, minutesRemaining.toInt())
            PrefsHelper.setTargetWarningNotifiedDate(this, todayDate)
        }

        // Then the actual "you reached it" alert
        val alreadyNotifiedToday = PrefsHelper.getTargetNotifiedDate(this) == todayDate
        if (today >= target && !alreadyNotifiedToday) {
            NotificationHelper.showTargetReachedAlert(this, target)
            PrefsHelper.setTargetNotifiedDate(this, todayDate)
        }
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        startForeground(
            NotificationHelper.FOREGROUND_NOTIFICATION_ID,
            NotificationHelper.buildForegroundNotification(this)
        )
        // Start slightly in the past so the very first check has events to read
        lastCheckedTime = System.currentTimeMillis() - checkIntervalMs
        handler.post(checkRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkForegroundApp() {
        val now = System.currentTimeMillis()
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = usm.queryEvents(lastCheckedTime, now)
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName ?: continue

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    if (pkg != currentPackage) {
                        currentPackage = pkg
                        sessionStartTime = event.timeStamp
                        alreadyNotified = false
                    }
                }
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (pkg == currentPackage) {
                        currentPackage = null
                        sessionStartTime = 0L
                        alreadyNotified = false
                    }
                }
            }
        }

        lastCheckedTime = now

        val pkg = currentPackage
        if (pkg != null && UsageStatsHelper.TRACKED_PACKAGES.contains(pkg) && sessionStartTime > 0) {
            val elapsed = now - sessionStartTime
            if (elapsed >= alertThresholdMs && !alreadyNotified) {
                val minutes = (elapsed / 1000 / 60).toInt()
                val appName = UsageStatsHelper.APP_NAMES[pkg] ?: pkg
                NotificationHelper.showScrollAlert(this, appName, minutes)
                alreadyNotified = true
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ScrollWatcherService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ScrollWatcherService::class.java))
        }
    }
}