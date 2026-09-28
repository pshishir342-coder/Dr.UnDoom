package com.example.drundoom

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import java.util.Calendar

object UsageStatsHelper {

    // The "doomscroll prone" apps we track. Add/remove package names as needed.
    val TRACKED_PACKAGES = listOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.twitter.android",
        "com.facebook.katana",
        "com.zhiliaoapp.musically" // TikTok
    )

    // Friendly display names for notifications
    val APP_NAMES = mapOf(
        "com.instagram.android" to "Instagram",
        "com.google.android.youtube" to "YouTube",
        "com.twitter.android" to "Twitter/X",
        "com.facebook.katana" to "Facebook",
        "com.zhiliaoapp.musically" to "TikTok"
    )

    fun hasUsagePermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // Builds an exact per-app foreground-duration map for [startTime, endTime)
    // using raw MOVE_TO_FOREGROUND / MOVE_TO_BACKGROUND events, instead of
    // trusting the OS's pre-aggregated INTERVAL_DAILY buckets (which can bleed
    // a few minutes across midnight). This is the accurate way to do it.
    private fun getPerAppMillis(context: Context, startTime: Long, endTime: Long): Map<String, Long> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = usm.queryEvents(startTime, endTime)

        val openTimestamps = mutableMapOf<String, Long>() // package -> when it came to foreground
        val totals = mutableMapOf<String, Long>()          // package -> accumulated millis

        val event = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName ?: continue
            if (!TRACKED_PACKAGES.contains(pkg)) continue

            when (event.eventType) {
                android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    openTimestamps[pkg] = event.timeStamp
                }
                android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    val openedAt = openTimestamps.remove(pkg)
                    if (openedAt != null && event.timeStamp > openedAt) {
                        totals[pkg] = (totals[pkg] ?: 0L) + (event.timeStamp - openedAt)
                    }
                }
            }
        }

        // Any app still open (foreground) when our window ends counts up to endTime
        for ((pkg, openedAt) in openTimestamps) {
            if (endTime > openedAt) {
                totals[pkg] = (totals[pkg] ?: 0L) + (endTime - openedAt)
            }
        }

        return totals
    }

    private fun startOfTodayMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Returns total minutes spent today (since midnight) across tracked apps
    fun getTodayUsageMinutes(context: Context): Long {
        val startTime = startOfTodayMillis()
        val endTime = System.currentTimeMillis()
        val totals = getPerAppMillis(context, startTime, endTime)
        val totalMillis = totals.values.sum()
        return totalMillis / 1000 / 60
    }

    // Returns the FULL total minutes for yesterday (midnight to midnight),
    // calculated straight from Android's usage events. This is the real
    // number, not a snapshot saved at some random moment.
    // Returns the full-day total minutes for a past day: daysAgo = 1 is
    // yesterday, 2 is the day before, and so on (midnight to midnight).
    fun getUsageMinutesDaysAgo(context: Context, daysAgo: Int): Long {
        val dayMillis = 24L * 60 * 60 * 1000
        val endTime = startOfTodayMillis() - (daysAgo - 1) * dayMillis
        val startTime = endTime - dayMillis
        val totals = getPerAppMillis(context, startTime, endTime)
        return totals.values.sum() / 1000 / 60
    }

    fun getYesterdayUsageMinutes(context: Context): Long = getUsageMinutesDaysAgo(context, 1)

    // DEBUG: returns "packageName: X min" per tracked app, for figuring out
    // which app is contributing how much to the total.
    fun getPerAppBreakdown(context: Context): List<String> {
        val startTime = startOfTodayMillis()
        val endTime = System.currentTimeMillis()
        val totals = getPerAppMillis(context, startTime, endTime)

        return totals.filter { it.value > 0 }
            .map { (pkg, millis) -> "$pkg: ${millis / 1000 / 60} min" }
    }

    // Returns (friendly app name, minutes used today) for each tracked app that
    // has any usage today, sorted from most-used to least-used. This is what
    // powers the Digital-Wellbeing-style bar chart on the dashboard.
    fun getPerAppUsageList(context: Context): List<Pair<String, Long>> {
        val startTime = startOfTodayMillis()
        val endTime = System.currentTimeMillis()
        val totals = getPerAppMillis(context, startTime, endTime)

        return totals
            .filter { it.value > 0 }
            .map { (pkg, millis) -> (APP_NAMES[pkg] ?: pkg) to (millis / 1000 / 60) }
            .sortedByDescending { it.second }
    }
}