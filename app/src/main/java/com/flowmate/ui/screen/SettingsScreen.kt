package com.flowmate.ui.screen

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.flowmate.ui.component.MainRoute
import com.flowmate.viewmodel.SettingsViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.Manifest
import androidx.core.net.toUri

// SettingsScreen is a composable function that displays the settings screen of the app.
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onNavigateTo: (MainRoute) -> Unit) {

    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current
    val notificationTime by viewModel.notificationTime.collectAsState()
    val versionName = getAppVersion(context)
    val isSoundOn by viewModel.isNotificationSoundOn.collectAsState()
    val quietHours by viewModel.quietHours.collectAsState()


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        // Daily Message Card
        NotificationSettingsRow(
            isNotificationsEnabled = isNotificationsEnabled,
            onToggle = { enabled -> viewModel.toggleNotifications(enabled) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Notification Sound")
            Switch(
                checked = isSoundOn,
                onCheckedChange = { viewModel.toggleNotificationSound(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Do Not Disturb Interval:")

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                TimePickerDialog(context, { _, hour, minute ->
                    viewModel.setQuietHours(LocalTime.of(hour, minute), quietHours.second)
                }, quietHours.first.hour, quietHours.first.minute, true).show()
            }) {
                Text("Start: ${quietHours.first.format(DateTimeFormatter.ofPattern("HH:mm"))}")
            }

            Button(onClick = {
                TimePickerDialog(context, { _, hour, minute ->
                    viewModel.setQuietHours(quietHours.first, LocalTime.of(hour, minute))
                }, quietHours.second.hour, quietHours.second.minute, true).show()
            }) {
                Text("End: ${quietHours.second.format(DateTimeFormatter.ofPattern("HH:mm"))}")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dark Theme")
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.toggleTheme(it) }
            )
        }
        // Notification Time
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNavigateTo(MainRoute.EditCredentials) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Edit Password / Username")
        }


        // Feedback Button
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:help@flowmate.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "FlowMate Feedback")
                putExtra(Intent.EXTRA_TEXT, "Hello, I have a problem about...")
            }
            context.startActivity(intent)
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)

        ) {
            Text("📩 Send Feedback")
        }

    // Reset Data Button
        Button(
            onClick = { viewModel.resetData() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Reset Data")
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Version and Developer Info
        HorizontalDivider()
        Text("📱 FlowMate v$versionName", modifier = Modifier.padding(top = 16.dp))
        Text("Developer: FlowMate Software Team")
    }
}
// NotificationSettingsRow is a composable function that displays a row with a switch to toggle notifications on or off.
@Composable
fun NotificationSettingsRow(
    isNotificationsEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var pendingToggle by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onToggle(true)
        else onToggle(false)

        pendingToggle = false
    }
    // If the user has previously requested permission and it was denied, we show a dialog to request permission again.
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Notifications")
        Switch(
            checked = isNotificationsEnabled,
            onCheckedChange = { isChecked ->
                if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = Manifest.permission.POST_NOTIFICATIONS
                    val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    if (!granted) {
                        pendingToggle = true
                        permissionLauncher.launch(permission)
                        return@Switch
                    }
                }
                onToggle(isChecked)
            }
        )
    }
}
// getAppVersion is a utility function that retrieves the app version name from the package manager.
fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}

