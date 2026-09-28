package com.example.drundoom

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrefsHelper {
    private const val PREFS_NAME = "doomscroll_prefs"
    private const val KEY_POINTS = "points"
    private const val KEY_STREAK = "streak"
    private const val KEY_LAST_DATE = "last_date"
    private const val KEY_YESTERDAY_USAGE = "yesterday_usage"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPoints(context: Context): Int = prefs(context).getInt(KEY_POINTS, 0)

    fun addPoints(context: Context, amount: Int) {
        val current = getPoints(context)
        val updated = (current + amount).coerceAtLeast(0)
        prefs(context).edit().putInt(KEY_POINTS, updated).apply()
    }

    fun getStreak(context: Context): Int = prefs(context).getInt(KEY_STREAK, 0)

    fun setStreak(context: Context, value: Int) {
        prefs(context).edit().putInt(KEY_STREAK, value).apply()
    }

    fun resetTokensAndStreak(context: Context) {
        prefs(context).edit()
            .putInt(KEY_POINTS, 0)
            .putInt(KEY_STREAK, 0)
            .apply()
    }

    // -1 means "no data yet" (first time opening the app)
    fun getYesterdayUsage(context: Context): Long =
        prefs(context).getLong(KEY_YESTERDAY_USAGE, -1L)

    fun setYesterdayUsage(context: Context, minutes: Long) {
        prefs(context).edit().putLong(KEY_YESTERDAY_USAGE, minutes).apply()
    }

    fun getLastCheckedDate(context: Context): String =
        prefs(context).getString(KEY_LAST_DATE, "") ?: ""

    fun setLastCheckedDate(context: Context, date: String) {
        prefs(context).edit().putString(KEY_LAST_DATE, date).apply()
    }

    private const val KEY_SCROLL_ALERTS_ENABLED = "scroll_alerts_enabled"

    fun getScrollAlertsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SCROLL_ALERTS_ENABLED, false)

    fun setScrollAlertsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_SCROLL_ALERTS_ENABLED, enabled).apply()
    }

    private const val KEY_TRACKING_ENABLED = "tracking_enabled"

    fun getTrackingEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_TRACKING_ENABLED, true)

    fun setTrackingEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_TRACKING_ENABLED, enabled).apply()
    }

    private const val KEY_TARGET_ENABLED = "target_enabled"
    private const val KEY_TARGET_MINUTES = "target_minutes"

    fun getTargetEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_TARGET_ENABLED, false)

    fun setTargetEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_TARGET_ENABLED, enabled).apply()
    }

    fun getTargetMinutes(context: Context): Int =
        prefs(context).getInt(KEY_TARGET_MINUTES, 120)

    fun setTargetMinutes(context: Context, minutes: Int) {
        prefs(context).edit().putInt(KEY_TARGET_MINUTES, minutes).apply()
    }

    private const val KEY_TARGET_NOTIFIED_DATE = "target_notified_date"

    fun getTargetNotifiedDate(context: Context): String =
        prefs(context).getString(KEY_TARGET_NOTIFIED_DATE, "") ?: ""

    fun setTargetNotifiedDate(context: Context, date: String) {
        prefs(context).edit().putString(KEY_TARGET_NOTIFIED_DATE, date).apply()
    }

    private const val KEY_TARGET_WARNING_NOTIFIED_DATE = "target_warning_notified_date"

    fun getTargetWarningNotifiedDate(context: Context): String =
        prefs(context).getString(KEY_TARGET_WARNING_NOTIFIED_DATE, "") ?: ""

    fun setTargetWarningNotifiedDate(context: Context, date: String) {
        prefs(context).edit().putString(KEY_TARGET_WARNING_NOTIFIED_DATE, date).apply()
    }

    fun todayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}