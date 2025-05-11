package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.SmartSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyHabitsViewModal : ViewModel() {

    // stubbed habits & suggestions
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _habitSuggestions = MutableStateFlow<List<SmartSuggestion>>(emptyList())
    val habitSuggestions: StateFlow<List<SmartSuggestion>> = _habitSuggestions.asStateFlow()

    init {
        loadStubData()
    }

    private fun loadStubData() {
        // populate some dummy habits & suggestions
        _habits.value = listOf(
            Habit("1", "Morning Run", 0.3f, isCompletedToday = false,5),
            Habit("2", "Read a Book", 0.6f, isCompletedToday = true,4)
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
    fun addHabit(habit: Habit) {
        _habits.value = _habits.value + habit
    }

}