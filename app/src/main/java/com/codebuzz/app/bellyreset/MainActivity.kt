package com.codebuzz.app.bellyreset

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.NavEntry
import androidx.lifecycle.viewmodel.compose.viewModel
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

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Whether granted or not, we attempt to schedule. 
        // Showing notification will fail gracefully if permission is missing.
        ReminderManager.scheduleReminders(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkNotificationPermission()
        ReminderManager.scheduleReminders(this)

        val repository = TrackerRepository(applicationContext)
        setContent {
            BellyResetTheme {
                MainScreen(repository)
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
fun MainScreen(repository: TrackerRepository) {
    val backStack = rememberNavBackStack(BellyResetNavKey.Today)
    
    val trackerViewModel: TrackerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TrackerViewModel(repository) as T
            }
        }
    )
    
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
                            backStack.clear()
                            backStack.add(item.key)
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
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
                    SettingsScreen()
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

