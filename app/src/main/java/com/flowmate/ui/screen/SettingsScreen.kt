package com.flowmate.ui.screen

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.MainRoute
import com.flowmate.viewmodel.SettingsViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

        /*** 2. Notification Settings ***/
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Notifications")
            Switch(checked = isNotificationsEnabled, onCheckedChange = {
                viewModel.toggleNotifications(it)
            })
        }

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

        /*** Edit password ***/
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNavigateTo(MainRoute.EditCredentials) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Edit Password / Username")
        }


        /*** 9. Send Feedback ***/
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:help@flowmate.com")
                putExtra(Intent.EXTRA_SUBJECT, "FlowMate Feedback")
                putExtra(Intent.EXTRA_TEXT, "Hello, I have a problem about...")
            }
            context.startActivity(intent)
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Change the button color

        ) {
            Text("📩 Send Feedback")
        }
        /*** 8. Reset Data ***/

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

        /*** 10. About App ***/
        Divider()
        Text("📱 FlowMate v$versionName", modifier = Modifier.padding(top = 16.dp))
        Text("Developer: Rolling Cat Software")
    }
}

/**
 * Get the app version name from the package manager.
 *
 * @param context The context of the application.
 * @return The version name of the app, or "1.0" if it cannot be retrieved.
 */
fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}

