package com.fastflow.app.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.res.stringResource
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
import com.fastflow.app.presentation.ramadan.RamadanScreen
import com.fastflow.app.presentation.settings.NotificationSettingsScreen
import com.fastflow.app.presentation.theme.FastFlowTheme
import com.fastflow.app.presentation.weight.WeightScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val bottomNavRoutes = setOf("home", "progress", "coach", "more", "profile")

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
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
            }

            LocalizedApp(languageTag = languageTag) {
                FastFlowTheme {
                    when (isOnboardingCompleted) {
                        true -> MainScreen()
                        false -> OnboardingFlow()
                        null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingFlow() {
    var showMainScreen by remember { mutableStateOf(false) }

    if (showMainScreen) {
        FastFlowTheme {
            MainScreen()
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
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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
            composable("home") { DashboardScreen() }
            composable("progress") { WeightScreen() }
            composable("coach") { CoachScreen() }
            composable("more") {
                MoreScreen(onNavigate = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                })
            }
            composable("profile") {
                ProfileScreen(
                    onOpenNotifications = { navController.navigate("notifications") }
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
                FastingHistoryScreen(onBack = { navController.popBackStack() })
            }
            composable("meal_plan") {
                MealPlanScreen(onBack = { navController.popBackStack() })
            }
            composable("health_sync") {
                HealthSyncScreen(onBack = { navController.popBackStack() })
            }
            composable("ramadan") {
                RamadanScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
