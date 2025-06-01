package com.flowmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.flowmate.ui.component.FlowMateNavGraph
import com.flowmate.ui.component.HabitType
import com.flowmate.ui.permissions.NotificationPermissionRequester
import com.flowmate.ui.theme.FlowMateTheme
import com.flowmate.viewmodel.SettingsViewModel
import com.flowmate.worker.ReminderWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel: SettingsViewModel by viewModels()

        setContent {
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            var askedOnce by remember { mutableStateOf(false) }

            FlowMateTheme(darkTheme = isDarkTheme) {
                NotificationPermissionRequester(
                    showRequest = !askedOnce,
                    onGranted = { askedOnce = true },
                    onDenied = { askedOnce = true }
                )

                FlowMateNavGraph(settingsViewModel = settingsViewModel)
            }

            val context = LocalContext.current

            // 🔔 Gerçek Daily Reminder test planlaması
            LaunchedEffect(Unit) {
                val reminderTime = LocalTime.now().plusMinutes(1) // şimdi + 1 dakika
                val now = LocalDateTime.now()
                val scheduledTime = LocalDateTime.of(LocalDate.now(), reminderTime)
                val delayMillis = Duration.between(now, scheduledTime).toMillis()

                ReminderWorker.enqueueReminder(
                    context = context,
                    title = "Test Daily Reminder",
                    delayMillis = delayMillis,
                    type = HabitType.DAILY,
                    reminderTime = reminderTime
                )

            }
        }
    }
}
