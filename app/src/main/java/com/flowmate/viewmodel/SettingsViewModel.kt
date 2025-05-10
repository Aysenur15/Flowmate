package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

class SettingsViewModel : ViewModel() {

    private val _isNotificationsEnabled = MutableStateFlow(true)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleNotifications(enabled: Boolean) {
        _isNotificationsEnabled.value = enabled
    }

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    private val _notificationTime = MutableStateFlow(LocalTime.of(9, 0))
    val notificationTime: StateFlow<LocalTime> = _notificationTime


    fun setNotificationTime(time: LocalTime) {
        _notificationTime.value = time
    }

    fun resetData() {
        // Burada Room DB ve local cache temizleme işlemleri yapılabilir
    }

    private val _isNotificationSoundOn = MutableStateFlow(true)
    val isNotificationSoundOn: StateFlow<Boolean> = _isNotificationSoundOn

    fun toggleNotificationSound(on: Boolean) {
        _isNotificationSoundOn.value = on
    }

    private val _quietHours = MutableStateFlow(Pair(LocalTime.of(22, 0), LocalTime.of(7, 0)))
    val quietHours: StateFlow<Pair<LocalTime, LocalTime>> = _quietHours

    fun setQuietHours(start: LocalTime, end: LocalTime) {
        _quietHours.value = Pair(start, end)
    }

}
