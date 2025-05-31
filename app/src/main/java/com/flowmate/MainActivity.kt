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
import java.time.LocalTime

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
            LaunchedEffect(Unit) {
                val delay = 10_000L // 10 saniye sonra bildirim
                val title = "Test Alarm"
                val type = HabitType.DAILY
                val time = LocalTime.now()

                ReminderWorker.enqueueReminder(
                    context = context,
                    title = title,
                    delayMillis = delay,
                    type = type,
                    reminderTime = time
                )
            }

        }

    }

}
