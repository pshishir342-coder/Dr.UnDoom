package com.example.drundoom

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.drundoom.ui.theme.DrUnDoomTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrUnDoomTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(UsageStatsHelper.hasUsagePermission(context)) }
    var selectedTab by remember { mutableStateOf(0) }
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // show splash for 2 seconds
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else if (!hasPermission) {
        PermissionScreen(
            onGrantClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            },
            onRecheckClick = {
                hasPermission = UsageStatsHelper.hasUsagePermission(context)
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Rewards") },
                        label = { Text("Rewards") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Lightbulb, contentDescription = "Tips") },
                        label = { Text("Tips") }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> DashboardScreen(onNavigateToTips = { selectedTab = 2 })
                    1 -> RewardsScreen()
                    else -> TipsScreen()
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "\uD83D\uDC26\u200D\uD83D\uDD25",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "DR.UNDOOM\n",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                "One step closer to a\nhealthier lifestyle",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}

data class ResourceItem(val title: String, val description: String, val url: String)

@Composable
fun TipsScreen() {
    val context = LocalContext.current

    val resources = listOf(
        ResourceItem(
            "Digital Minimalism",
            "Videos on Cal Newport's approach to intentional tech use. CLICK ON THE BAR.",
            "https://www.youtube.com/results?search_query=digital+minimalism+cal+newport"
        ),
        ResourceItem(
            "Why Your Phone Is Addictive",
            "The psychology and design tricks behind phone addiction. CLICK ON THE BAR.",
            "https://www.youtube.com/results?search_query=why+your+phone+is+addictive"
        ),
        ResourceItem(
            "How to Break Up With Your Phone",
            "Practical, actionable steps to cut down screen time. CLICK ON THE BAR.",
            "https://www.youtube.com/results?search_query=how+to+break+up+with+your+phone"
        ),
        ResourceItem(
            "Dopamine Detox Explained.",
            "Understanding dopamine loops and resetting your attention span. CLICK ON THE BAR.",
            "https://www.youtube.com/results?search_query=dopamine+detox+screen+time"
        ),
        ResourceItem(
            "Google's Digital Wellbeing Tools.",
            "Official tips and built-in tools for healthier phone habits. CLICK ON THE BAR.",
            "https://wellbeing.google/"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFF3F51B5))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "Reduce Screen Time",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White
        )
        Text(
            "Helpful videos and guides",
            fontSize = 14.sp,
            color = androidx.compose.ui.graphics.Color.Magenta
        )
        Spacer(modifier = Modifier.height(20.dp))

        resources.forEach { resource ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resource.url)))
                    },
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(resource.title, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(resource.description, fontSize = 12.sp, color = androidx.compose.ui.graphics.Color.DarkGray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PermissionScreen(onGrantClick: () -> Unit, onRecheckClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("One-time setup", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "DrUnDoom needs Usage Access permission to track your screen time on doomscroll-prone apps.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGrantClick) {
            Text("Open Settings & Grant Access")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onRecheckClick) {
            Text("I already granted it — Continue")
        }
    }
}

@Composable
fun backgroundBrushForUsage(minutes: Long): androidx.compose.ui.graphics.Brush {
    val baseColor = when {
        minutes < 90 -> androidx.compose.ui.graphics.Color(0xFF66BB6A) // green
        minutes < 180 -> androidx.compose.ui.graphics.Color(0xFFFFF176) // yellow
        else -> androidx.compose.ui.graphics.Color(0xFFEF5350) // red
    }
    return androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(baseColor, androidx.compose.ui.graphics.Color.White)
    )
}

@Composable
fun DashboardScreen(onNavigateToTips: () -> Unit) {
    val context = LocalContext.current

    var todayMinutes by remember { mutableStateOf(0L) }
    var yesterdayMinutes by remember { mutableStateOf(0L) }
    var appUsageList by remember { mutableStateOf(listOf<Pair<String, Long>>()) }
    var points by remember { mutableStateOf(0) }
    var todayTokens by remember { mutableStateOf(50) }
    var streak by remember { mutableStateOf(0) }
    var nudge by remember { mutableStateOf("") }
    var trackingEnabled by remember { mutableStateOf(PrefsHelper.getTrackingEnabled(context)) }
    var scrollAlertsEnabled by remember { mutableStateOf(PrefsHelper.getScrollAlertsEnabled(context)) }
    var targetEnabled by remember { mutableStateOf(PrefsHelper.getTargetEnabled(context)) }
    var targetMinutes by remember { mutableStateOf(PrefsHelper.getTargetMinutes(context)) }
    var targetInputText by remember { mutableStateOf(PrefsHelper.getTargetMinutes(context).toString()) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ScrollWatcherService.start(context)
            scrollAlertsEnabled = true
            PrefsHelper.setScrollAlertsEnabled(context, true)
        }
    }

    val targetNotificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ScrollWatcherService.start(context)
            targetEnabled = true
            PrefsHelper.setTargetEnabled(context, true)
        }
    }

    var refreshTick by remember { mutableStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTick++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(trackingEnabled, refreshTick) {
        if (!trackingEnabled) {
            // Master off: make sure the background watcher is fully stopped,
            // and just show last saved points/streak, nothing live.
            ScrollWatcherService.stop(context)
            points = PrefsHelper.getPoints(context)
            streak = PrefsHelper.getStreak(context)
            nudge = "Tracking is paused"
            todayMinutes = 0L
            yesterdayMinutes = 0L
            appUsageList = emptyList()
            return@LaunchedEffect
        }

        // If the service should be running based on saved settings (e.g. after
        // the app was closed/restarted), make sure it's actually alive.
        if (PrefsHelper.getScrollAlertsEnabled(context) || PrefsHelper.getTargetEnabled(context)) {
            ScrollWatcherService.start(context)
        }

        val today = UsageStatsHelper.getTodayUsageMinutes(context)
        val todayDate = PrefsHelper.todayDateString()
        val lastCheckedDate = PrefsHelper.getLastCheckedDate(context)
        // Yesterday's REAL full-day total, read straight from Android's usage
        // events (not a snapshot saved at some random moment).
        val yesterdayUsage = UsageStatsHelper.getYesterdayUsageMinutes(context)

        // The day before yesterday, needed to score yesterday when banking tokens
        val dayBeforeUsage = UsageStatsHelper.getUsageMinutesDaysAgo(context, 2)

        // Only used when there is no previous day to compare against (e.g. the
        // very first day): falls back to your Daily Target, else 180 minutes.
        val fallbackReferenceMinutes: Long = if (PrefsHelper.getTargetEnabled(context))
            PrefsHelper.getTargetMinutes(context).toLong()
        else
            180L

        // 50 tokens when usage is 0, dropping in a straight line to 0 tokens
        // when usage reaches the baseline (the previous day's screen time).
        fun calculateTokens(minutesUsed: Long, baselineMinutes: Long): Int {
            val reference = if (baselineMinutes > 0) baselineMinutes else fallbackReferenceMinutes
            val ratio = (minutesUsed.toDouble() / reference).coerceIn(0.0, 1.0)
            return (50 * (1 - ratio)).toInt()
        }

        if (lastCheckedDate != todayDate) {
            // Day just rolled over: bank yesterday's tokens (yesterday compared
            // with the day before) into the wallet. Skipped on the first launch.
            if (lastCheckedDate.isNotEmpty()) {
                val earnedYesterday = calculateTokens(yesterdayUsage, dayBeforeUsage)
                PrefsHelper.addPoints(context, earnedYesterday)
                if (earnedYesterday > 0) {
                    PrefsHelper.setStreak(context, PrefsHelper.getStreak(context) + 1)
                } else {
                    PrefsHelper.setStreak(context, 0)
                }
            }
            PrefsHelper.setLastCheckedDate(context, todayDate)
        }

        // Live: today compared with yesterday, updates as today's usage grows
        todayTokens = calculateTokens(today, yesterdayUsage)

        todayMinutes = today
        yesterdayMinutes = yesterdayUsage
        appUsageList = UsageStatsHelper.getPerAppUsageList(context)
        points = PrefsHelper.getPoints(context)
        streak = PrefsHelper.getStreak(context)
        nudge = NudgeEngine.getNudge(today, if (yesterdayUsage > 0) yesterdayUsage else -1L, streak)
    }

    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.material3.LocalContentColor provides androidx.compose.ui.graphics.Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrushForUsage(todayMinutes))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Usage Tracking", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Magenta)
                        Text(
                            if (trackingEnabled) "Actively tracking your usage" else "Tracking paused",
                            fontSize = 12.sp,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                    Switch(
                        checked = trackingEnabled,
                        onCheckedChange = { checked ->
                            trackingEnabled = checked
                            PrefsHelper.setTrackingEnabled(context, checked)

                            if (!checked) {
                                // Master off switch — kill the background watcher
                                // entirely, regardless of the other toggles.
                                ScrollWatcherService.stop(context)
                            } else {
                                // Resume background watching only if the user's
                                // other settings actually want it running.
                                if (PrefsHelper.getScrollAlertsEnabled(context) || PrefsHelper.getTargetEnabled(context)) {
                                    ScrollWatcherService.start(context)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            StatCard(label = "Doom Tokens", value = "$points \uD83E\uDE99")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Reset tokens",
                fontSize = 11.sp,
                color = androidx.compose.ui.graphics.Color.DarkGray,
                modifier = Modifier.clickable {
                    PrefsHelper.resetTokensAndStreak(context)
                    points = 0
                    streak = 0
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "Today's Tokens (of 50)", value = "$todayTokens \uD83E\uDE99")
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "Streak", value = "$streak \uD83D\uDD25")
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "Today's usage", value = "$todayMinutes min")

            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { if (streak >= 7) 1f else streak / 7f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (todayMinutes == yesterdayMinutes)
                        androidx.compose.ui.graphics.Color.Gray
                    else
                        CardDefaults.cardColors().containerColor
                )
            ) {
                Text(nudge, modifier = Modifier.padding(16.dp), fontSize = 16.sp, color = when {
                    nudge.startsWith("Nice") -> androidx.compose.ui.graphics.Color(0xFF3FA9DC) // arctic blue, only for the "Nice, you're using less..." message
                    todayMinutes == yesterdayMinutes -> androidx.compose.ui.graphics.Color(0xFF4A148C)
                    else -> androidx.compose.ui.graphics.Color(0xFF6A1B9A)
                })
            }

            Spacer(modifier = Modifier.height(24.dp))

            UsageComparisonBar(todayMinutes = todayMinutes, yesterdayMinutes = yesterdayMinutes)

            Spacer(modifier = Modifier.height(24.dp))

            AppUsageChart(appUsageList = appUsageList)

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily Target", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Gray)
                            Text("Set a screen-time goal for tracked apps", fontSize = 12.sp, color = androidx.compose.ui.graphics.Color(0xFF6A1B9A))
                        }
                        Switch(
                            checked = targetEnabled,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        targetNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        ScrollWatcherService.start(context)
                                        targetEnabled = true
                                        PrefsHelper.setTargetEnabled(context, true)
                                    }
                                } else {
                                    targetEnabled = false
                                    PrefsHelper.setTargetEnabled(context, false)
                                    // Only stop the service if scroll alerts isn't also using it
                                    if (!PrefsHelper.getScrollAlertsEnabled(context)) {
                                        ScrollWatcherService.stop(context)
                                    }
                                }
                            }
                        )
                    }

                    if (targetEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = targetInputText,
                            onValueChange = { newValue ->
                                targetInputText = newValue
                                val parsed = newValue.toIntOrNull()
                                if (parsed != null && parsed > 0) {
                                    targetMinutes = parsed
                                    PrefsHelper.setTargetMinutes(context, parsed)
                                    // New target = fresh goal, so clear today's
                                    // "already notified" flags to allow re-firing
                                    PrefsHelper.setTargetNotifiedDate(context, "")
                                    PrefsHelper.setTargetWarningNotifiedDate(context, "")
                                }
                            },
                            label = { Text("Target (minutes)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val progress = if (targetMinutes > 0) (todayMinutes.toFloat() / targetMinutes).coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (todayMinutes >= targetMinutes)
                                androidx.compose.ui.graphics.Color.Red
                            else
                                androidx.compose.ui.graphics.Color(0xFF42A5F5)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "$todayMinutes / $targetMinutes min used",
                            fontSize = 13.sp,
                            color = androidx.compose.ui.graphics.Color.Black
                        )

                        if (todayMinutes >= targetMinutes) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "You've hit your target for today \u26A0\uFE0F",
                                fontSize = 13.sp,
                                color = androidx.compose.ui.graphics.Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Need ideas to cut down? Tap for tips \u2192",
                            fontSize = 13.sp,
                            color = androidx.compose.ui.graphics.Color(0xFFFFC0CB),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToTips() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Scroll alerts", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                        Text("Notify me after 15 min straight in one app", fontSize = 12.sp, color = androidx.compose.ui.graphics.Color.White)
                    }
                    Switch(
                        checked = scrollAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    ScrollWatcherService.start(context)
                                    scrollAlertsEnabled = true
                                    PrefsHelper.setScrollAlertsEnabled(context, true)
                                }
                            } else {
                                scrollAlertsEnabled = false
                                PrefsHelper.setScrollAlertsEnabled(context, false)
                                // Only stop the service if the target feature isn't also using it
                                if (!PrefsHelper.getTargetEnabled(context)) {
                                    ScrollWatcherService.stop(context)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun UsageComparisonBar(todayMinutes: Long, yesterdayMinutes: Long) {
    val maxValue = maxOf(todayMinutes, yesterdayMinutes, 1L).toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today vs Yesterday", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            UsageBarRow(label = "Today", minutes = todayMinutes, fraction = todayMinutes / maxValue, barColor = androidx.compose.ui.graphics.Color(0xFF42A5F5), textColor = androidx.compose.ui.graphics.Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            UsageBarRow(label = "Yesterday", minutes = yesterdayMinutes, fraction = yesterdayMinutes / maxValue, barColor = androidx.compose.ui.graphics.Color(0xFF9E9E9E), textColor = androidx.compose.ui.graphics.Color(0xFF2E7D32))
        }
    }
}

@Composable
fun UsageBarRow(label: String, minutes: Long, fraction: Float, barColor: androidx.compose.ui.graphics.Color, textColor: androidx.compose.ui.graphics.Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 13.sp, color = textColor, fontWeight = FontWeight.Bold)
            Text("$minutes min", fontSize = 13.sp, color = textColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(androidx.compose.ui.graphics.Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(barColor, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun AppUsageChart(appUsageList: List<Pair<String, Long>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("App Usage Today", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            if (appUsageList.isEmpty()) {
                Text(
                    "No tracked-app usage yet today",
                    fontSize = 13.sp,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            } else {
                val maxMinutes = appUsageList.maxOf { it.second }.coerceAtLeast(1L).toFloat()
                val maxBarHeight = 140.dp
                val barColors = listOf(
                    androidx.compose.ui.graphics.Color(0xFFE91E63),
                    androidx.compose.ui.graphics.Color(0xFF3F51B5),
                    androidx.compose.ui.graphics.Color(0xFFFF9800),
                    androidx.compose.ui.graphics.Color(0xFF009688),
                    androidx.compose.ui.graphics.Color(0xFF9C27B0)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(maxBarHeight + 48.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    appUsageList.forEachIndexed { index, (appName, minutes) ->
                        val barHeight = maxBarHeight * (minutes / maxMinutes)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text("$minutes", fontSize = 11.sp, color = androidx.compose.ui.graphics.Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(barHeight.coerceAtLeast(4.dp))
                                    .background(
                                        barColors[index % barColors.size],
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                            topStart = 6.dp, topEnd = 6.dp
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                appName,
                                fontSize = 10.sp,
                                color = androidx.compose.ui.graphics.Color.Black,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 16.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

data class Reward(val title: String, val cost: String)

@Composable
fun RewardsScreen() {
    val rewards = listOf(
        Reward("₹50 off next order", "500 Doom Tokens"),
        Reward("1 Free Coffee", "300 Doom Tokens"),
        Reward("10% Movie Ticket Discount", "800 Doom Tokens"),
        Reward("Mystery Reward", "1000 Doom Tokens")
    )
    var showDialog by remember { mutableStateOf(false) }
    var redeemedTitle by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Rewards", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        rewards.forEach { reward ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(reward.title, fontWeight = FontWeight.Bold)
                        Text(reward.cost, fontSize = 12.sp)
                    }
                    Button(onClick = {
                        redeemedTitle = reward.title
                        showDialog = true
                    }) {
                        Text("Redeem")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            },
            title = { Text("Redeemed!") },
            text = { Text("$redeemedTitle redeemed (Demo only — no real reward sent).") }
        )
    }
}