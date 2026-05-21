package com.fastflow.app.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fastflow.app.presentation.components.OneFastLogo
import com.fastflow.app.presentation.components.OneFastLogoVariant
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fastflow.app.R
import com.fastflow.app.core.locale.AppLocaleManager
import com.fastflow.app.data.preferences.PreferencesManager
import com.fastflow.app.presentation.challenges.ChallengesScreen
import com.fastflow.app.presentation.coach.CoachScreen
import com.fastflow.app.presentation.community.CommunityScreen
import com.fastflow.app.presentation.dashboard.DashboardScreen
import com.fastflow.app.presentation.healthsync.HealthSyncScreen
import com.fastflow.app.presentation.history.FastingHistoryScreen
import com.fastflow.app.presentation.localization.LocalizedApp
import com.fastflow.app.presentation.meal.MealPlanScreen
import com.fastflow.app.presentation.more.MoreScreen
import com.fastflow.app.presentation.onboarding.OnboardingScreen
import com.fastflow.app.presentation.profile.ProfileScreen
import com.fastflow.app.presentation.settings.SettingsScreen
import com.fastflow.app.presentation.ramadan.RamadanScreen
import com.fastflow.app.presentation.settings.NotificationSettingsScreen
import com.fastflow.app.presentation.theme.FastFlowTheme
import com.fastflow.app.presentation.tips.TipsScreen
import com.fastflow.app.presentation.hydration.HydrationScreen
import com.fastflow.app.presentation.pricing.PricingScreen
import com.fastflow.app.presentation.weight.WeightScreen
import com.fastflow.app.presentation.components.ExactAlarmPermissionDialog
import com.fastflow.app.presentation.components.NotificationPermissionDialog
import com.fastflow.app.presentation.components.POST_NOTIFICATIONS_PERMISSION
import android.app.AlarmManager
import android.content.Intent
import android.provider.Settings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.rememberCoroutineScope
import javax.inject.Inject

private val bottomNavRoutes = setOf("home", "progress", "coach", "more", "profile")

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        super.onCreate(savedInstanceState)
        val language = runBlocking {
            preferencesManager.getAppLanguageOnce()
                ?: AppLocaleManager.defaultLanguageTag()
        }
        AppLocaleManager.apply(language)

        setContent {
            var languageTag by remember { mutableStateOf(language) }
            var isOnboardingCompleted by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(Unit) {
                isOnboardingCompleted = preferencesManager.isOnboardingCompleted.first()
                keepSplashOnScreen = false
            }

            LocalizedApp(languageTag = languageTag) {
                FastFlowTheme {
                    when (isOnboardingCompleted) {
                        true -> MainScreen(
                            preferencesManager = preferencesManager,
                            promptNotifications = false
                        )
                        false -> OnboardingFlow(preferencesManager = preferencesManager)
                        null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                OneFastLogo(
                                    variant = OneFastLogoVariant.Full,
                                    width = 220.dp,
                                    height = 96.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingFlow(preferencesManager: PreferencesManager) {
    var showMainScreen by remember { mutableStateOf(false) }

    if (showMainScreen) {
        FastFlowTheme {
            MainScreen(
                preferencesManager = preferencesManager,
                promptNotifications = true
            )
        }
    } else {
        FastFlowTheme(darkTheme = true) {
            OnboardingScreen(
                onComplete = { showMainScreen = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    preferencesManager: PreferencesManager,
    promptNotifications: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val openPricing: () -> Unit = {
        navController.navigate("pricing") { launchSingleTop = true }
    }

    var showNotificationDialog by remember { mutableStateOf(false) }
    var showExactAlarmDialog by remember { mutableStateOf(false) }
    val alarmManager = remember { context.getSystemService(AlarmManager::class.java) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        scope.launch { preferencesManager.setNotificationPermissionAsked(true) }
    }

    LaunchedEffect(promptNotifications) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyAsked = preferencesManager.getNotificationPermissionAskedOnce()
            if (promptNotifications || !alreadyAsked) {
                showNotificationDialog = true
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val exactAlarmAsked = preferencesManager.getExactAlarmPermissionAskedOnce()
            val canSchedule = alarmManager?.canScheduleExactAlarms() == true
            if (!exactAlarmAsked && !canSchedule) {
                showExactAlarmDialog = true
            }
        }
    }

    if (showNotificationDialog) {
        NotificationPermissionDialog(
            onConfirm = {
                showNotificationDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(POST_NOTIFICATIONS_PERMISSION)
                } else {
                    scope.launch { preferencesManager.setNotificationPermissionAsked(true) }
                }
            },
            onDismiss = {
                showNotificationDialog = false
                scope.launch { preferencesManager.setNotificationPermissionAsked(true) }
            }
        )
    }

    if (showExactAlarmDialog) {
        ExactAlarmPermissionDialog(
            onConfirm = {
                showExactAlarmDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = android.net.Uri.parse("package:${context.packageName}")
                        }
                    )
                }
                scope.launch { preferencesManager.setExactAlarmPermissionAsked(true) }
            },
            onDismiss = {
                showExactAlarmDialog = false
                scope.launch { preferencesManager.setExactAlarmPermissionAsked(true) }
            }
        )
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_progress)) },
                        selected = currentRoute == "progress",
                        onClick = {
                            navController.navigate("progress") {
                                popUpTo("home")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_coach)) },
                        selected = currentRoute == "coach",
                        onClick = {
                            navController.navigate("coach") {
                                popUpTo("home")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Apps, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_more)) },
                        selected = currentRoute == "more",
                        onClick = {
                            navController.navigate("more") {
                                popUpTo("home")
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_profile)) },
                        selected = currentRoute == "profile",
                        onClick = {
                            navController.navigate("profile") {
                                popUpTo("home")
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { DashboardScreen(onOpenPricing = openPricing) }
            composable("progress") { WeightScreen() }
            composable("coach") { CoachScreen() }
            composable("more") {
                MoreScreen(onNavigate = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                })
            }
            composable("profile") {
                ProfileScreen(
                    onOpenSettings = { navController.navigate("settings") },
                    onOpenPricing = openPricing
                )
            }
            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenNotifications = { navController.navigate("notifications") },
                    onOpenPricing = openPricing
                )
            }
            composable("notifications") {
                NotificationSettingsScreen(onBack = { navController.popBackStack() })
            }
            composable("challenges") {
                ChallengesScreen(onBack = { navController.popBackStack() })
            }
            composable("community") {
                CommunityScreen(onBack = { navController.popBackStack() })
            }
            composable("history") {
                FastingHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPricing = openPricing
                )
            }
            composable("meal_plan") {
                MealPlanScreen(onBack = { navController.popBackStack() })
            }
            composable("health_sync") {
                HealthSyncScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPricing = openPricing
                )
            }
            composable("ramadan") {
                RamadanScreen(onBack = { navController.popBackStack() })
            }
            composable("tips") {
                TipsScreen(onBack = { navController.popBackStack() })
            }
            composable("hydration") {
                HydrationScreen(onBack = { navController.popBackStack() })
            }
            composable("pricing") {
                PricingScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
