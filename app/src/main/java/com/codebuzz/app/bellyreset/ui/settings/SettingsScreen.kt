package com.codebuzz.app.bellyreset.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codebuzz.app.bellyreset.data.model.AppSettings
import com.codebuzz.app.bellyreset.data.model.ThemeMode
import com.codebuzz.app.bellyreset.ui.components.BellyResetTopBar
import com.codebuzz.app.bellyreset.ui.tracker.TrackerViewModel

@Composable
fun SettingsScreen(viewModel: TrackerViewModel) {
    val storedSettings by viewModel.settings.collectAsState()
    val settings = storedSettings ?: AppSettings()

    val context = LocalContext.current
    var notificationsAllowed by remember { mutableStateOf(true) }
    // Re-check on resume so returning from the permission prompt or system settings updates the hint
    LifecycleResumeEffect(Unit) {
        notificationsAllowed = NotificationManagerCompat.from(context).areNotificationsEnabled()
        onPauseOrDispose { }
    }

    Scaffold(
        topBar = { BellyResetTopBar(title = "Settings") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SettingToggle(
                title = "Reminders",
                description = "A nudge at 9 PM to close out your day, and a waist and weight check on Sunday mornings.",
                icon = Icons.Rounded.Notifications,
                checked = settings.remindersEnabled,
                onCheckedChange = { viewModel.setRemindersEnabled(it) }
            )

            if (settings.remindersEnabled && !notificationsAllowed) {
                Column(modifier = Modifier.padding(start = 40.dp, top = 4.dp)) {
                    Text(
                        text = "Notifications are turned off for Belly Reset, so reminders won't show.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Open notification settings")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            ThemeSetting(
                selected = settings.themeMode,
                onSelect = { viewModel.setThemeMode(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSetting(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    val options = listOf(
        ThemeMode.SYSTEM to "System",
        ThemeMode.LIGHT to "Light",
        ThemeMode.DARK to "Dark"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
    }
    Spacer(modifier = Modifier.height(12.dp))
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (mode, label) ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(label)
            }
        }
    }
}

@Composable
fun SettingToggle(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1.0f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = null)
    }
}
