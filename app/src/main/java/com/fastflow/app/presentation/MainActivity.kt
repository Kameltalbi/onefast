package com.fastflow.app.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.fastflow.app.presentation.coach.CoachScreen
import com.fastflow.app.presentation.dashboard.DashboardScreen
import com.fastflow.app.presentation.onboarding.OnboardingScreen
import com.fastflow.app.presentation.profile.ProfileScreen
import com.fastflow.app.presentation.settings.NotificationSettingsScreen
import com.fastflow.app.presentation.theme.FastFlowTheme
import com.fastflow.app.presentation.weight.WeightScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking {
            val language = preferencesManager.getAppLanguageOnce()
                ?: AppLocaleManager.defaultLanguageTag()
            AppLocaleManager.apply(language)
        }
        super.onCreate(savedInstanceState)
        setContent {
            FastFlowTheme {
                val isOnboardingCompleted = runBlocking {
                    preferencesManager.isOnboardingCompleted.first()
                }

                if (isOnboardingCompleted) {
                    FastFlowTheme {
                        MainScreen()
                    }
                } else {
                    OnboardingFlow()
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
            if (currentRoute == "home" || currentRoute == "progress" ||
                currentRoute == "coach" || currentRoute == "profile"
            ) {
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
            composable("profile") {
                ProfileScreen(
                    onOpenNotifications = { navController.navigate("notifications") }
                )
            }
            composable("notifications") {
                NotificationSettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
