package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

class SettingsViewModel : ViewModel() {

    // Notifications On/Off
    private val _isNotificationsEnabled = MutableStateFlow(true)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled
    fun toggleNotifications(enabled: Boolean) {
        _isNotificationsEnabled.value = enabled
    }

    // Notification Sound On/Off
    private val _isNotificationSoundOn = MutableStateFlow(true)
    val isNotificationSoundOn: StateFlow<Boolean> = _isNotificationSoundOn
    fun toggleNotificationSound(on: Boolean) {
        _isNotificationSoundOn.value = on
    }

    // Dark Theme
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme
    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    // Quiet Hours
    private val _quietHours = MutableStateFlow(Pair(LocalTime.of(22, 0), LocalTime.of(7, 0)))
    val quietHours: StateFlow<Pair<LocalTime, LocalTime>> = _quietHours
    fun setQuietHours(start: LocalTime, end: LocalTime) {
        _quietHours.value = Pair(start, end)
    }

    // Notification Time (for scheduling reminders, if needed)
    private val _notificationTime = MutableStateFlow(LocalTime.of(9, 0))
    val notificationTime: StateFlow<LocalTime> = _notificationTime
    fun setNotificationTime(time: LocalTime) {
        _notificationTime.value = time
    }

    // Reset All App Settings (extend this for database/cache reset)
    fun resetData() {
        _isNotificationsEnabled.value = false
        _isNotificationSoundOn.value = true
        _isDarkTheme.value = false
        _notificationTime.value = LocalTime.of(9, 0)
        _quietHours.value = Pair(LocalTime.of(22, 0), LocalTime.of(7, 0))

        // TODO: Add Room database/cache clearing logic here if needed
    }
}
