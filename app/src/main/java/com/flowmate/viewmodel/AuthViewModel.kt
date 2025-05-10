package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.ui.screen.Habit
import com.flowmate.ui.screen.SmartSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor() : ViewModel() {

    // outside-app vs inside-app flag
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    // for greeting on HomeScreen
    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // stubbed habits & suggestions
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _habitSuggestions = MutableStateFlow<List<SmartSuggestion>>(emptyList())
    val habitSuggestions: StateFlow<List<SmartSuggestion>> = _habitSuggestions.asStateFlow()

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            // TODO: call your real auth repo
            if (username.isNotBlank() && password.isNotBlank()) {
                _currentUserName.value = username
                _isUserLoggedIn.value = true
                loadStubData()
            }
        }
    }

    fun signUp(name: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            // TODO: real sign-up logic
            _currentUserName.value = name
            _isUserLoggedIn.value = true
            loadStubData()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isUserLoggedIn.value = false
            _currentUserName.value = ""
            _habits.value = emptyList()
        }
    }

    private fun loadStubData() {
        // populate some dummy habits & suggestions
        _habits.value = listOf(
            Habit("1", "Morning Run", 0.3f, isCompletedToday = false),
            Habit("2", "Read a Book", 0.6f, isCompletedToday = true)
        )
        _habitSuggestions.value = listOf(
            SmartSuggestion("s1", "Try a 5-minute journaling habit"),
            SmartSuggestion("s2", "Add a midday stretch break")
        )
    }

    fun toggleHabitCompletion(habitId: String) {
        val updated = _habits.value.map { habit ->
            if (habit.id == habitId) {
                habit.copy(
                    isCompletedToday = !habit.isCompletedToday,
                    weeklyProgress = if (!habit.isCompletedToday)
                        (habit.weeklyProgress + 1f / 7f).coerceAtMost(1f)
                    else
                        (habit.weeklyProgress - 1f / 7f).coerceAtLeast(0f)
                )
            } else habit
        }
        _habits.value = updated
    }
}
