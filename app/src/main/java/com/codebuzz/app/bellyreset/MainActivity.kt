package com.codebuzz.app.bellyreset

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codebuzz.app.bellyreset.data.model.ThemeMode
import com.codebuzz.app.bellyreset.data.repository.TrackerRepository
import com.codebuzz.app.bellyreset.notifications.ReminderManager
import com.codebuzz.app.bellyreset.ui.navigation.BellyResetNavKey
import com.codebuzz.app.bellyreset.ui.plan.PlanScreen
import com.codebuzz.app.bellyreset.ui.progress.ProgressScreen
import com.codebuzz.app.bellyreset.ui.settings.SettingsScreen
import com.codebuzz.app.bellyreset.ui.theme.BellyResetTheme
import com.codebuzz.app.bellyreset.ui.today.TodayScreen
import com.codebuzz.app.bellyreset.ui.tracker.TrackerViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Same scrims enableEdgeToEdge() uses by default for the navigation bar
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Reminders are scheduled either way.
        // Showing notification will fail gracefully if permission is missing.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TrackerRepository(applicationContext)
        setContent {
            val trackerViewModel: TrackerViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TrackerViewModel(repository) as T
                    }
                }
            )
            val settings by trackerViewModel.settings.collectAsState()

            val darkTheme = when (settings?.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                else -> isSystemInDarkTheme()
            }
            // Keep system bar icons readable when the app theme differs from the system theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(lightScrim, darkScrim) { darkTheme }
                )
                onDispose { }
            }

            val remindersEnabled = settings?.remindersEnabled
            LaunchedEffect(remindersEnabled) {
                when (remindersEnabled) {
                    true -> {
                        checkNotificationPermission()
                        ReminderManager.scheduleReminders(this@MainActivity)
                    }
                    false -> ReminderManager.cancelReminders(this@MainActivity)
                    null -> Unit // settings not loaded yet
                }
            }

            BellyResetTheme(darkTheme = darkTheme) {
                MainScreen(trackerViewModel)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainScreen(trackerViewModel: TrackerViewModel) {
    val backStack = rememberNavBackStack(BellyResetNavKey.Today)

    val items = listOf(
        NavigationItem(
            key = BellyResetNavKey.Today,
            label = "Today",
            icon = Icons.Rounded.CalendarToday
        ),
        NavigationItem(
            key = BellyResetNavKey.Progress,
            label = "Progress",
            icon = Icons.Rounded.Timeline
        ),
        NavigationItem(
            key = BellyResetNavKey.Plan,
            label = "Plan",
            icon = Icons.AutoMirrored.Rounded.ListAlt
        ),
        NavigationItem(
            key = BellyResetNavKey.Settings,
            label = "Settings",
            icon = Icons.Rounded.Settings
        )
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach { item ->
                item(
                    selected = backStack.last() == item.key,
                    onClick = {
                        if (backStack.last() != item.key) {
                            // Today stays at the bottom of the stack so Back returns to it before exiting
                            while (backStack.size > 1) {
                                backStack.removeAt(backStack.size - 1)
                            }
                            if (item.key != BellyResetNavKey.Today) {
                                backStack.add(item.key)
                            }
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) }
                )
            }
        }
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeAt(backStack.size - 1) },
            entryProvider = entryProvider {
                entry<BellyResetNavKey.Today> {
                    TodayScreen(trackerViewModel)
                }
                entry<BellyResetNavKey.Progress> {
                    ProgressScreen(trackerViewModel)
                }
                entry<BellyResetNavKey.Plan> {
                    PlanScreen()
                }
                entry<BellyResetNavKey.Settings> {
                    SettingsScreen(trackerViewModel)
                }
            }
        )
    }
}

data class NavigationItem(
    val key: BellyResetNavKey,
    val label: String,
    val icon: ImageVector
)
