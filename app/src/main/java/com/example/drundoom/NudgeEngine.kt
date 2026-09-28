package com.example.drundoom

object NudgeEngine {

    fun getNudge(todayMinutes: Long, yesterdayMinutes: Long, streak: Int): String {
        return when {
            yesterdayMinutes < 0 -> "First day tracking — let's see how you do!"
            todayMinutes < yesterdayMinutes && streak >= 3 ->
                "🔥 $streak day streak! You're crushing it, keep going."
            todayMinutes < yesterdayMinutes ->
                "Nice, you're using less than yesterday. Keep it up!"
            todayMinutes == yesterdayMinutes ->
                "Same as yesterday. Try to beat it tomorrow."
            else ->
                "You're slipping — usage is higher than yesterday. Put the phone down 👀"
        }
    }
}
