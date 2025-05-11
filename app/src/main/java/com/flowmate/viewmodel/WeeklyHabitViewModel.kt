package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.ui.component.HabitStatus
import com.flowmate.ui.component.WeeklyHabit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class WeeklyHabitViewModel : ViewModel() {

    private val _weeklyHabits = MutableStateFlow<List<WeeklyHabit>>(emptyList())
    val weeklyHabits: StateFlow<List<WeeklyHabit>> = _weeklyHabits.asStateFlow()

    // Get current week range (Mon to Sun)
    private val currentWeekDays = DayOfWeek.values().toList()

    init {
        loadStubData()
    }

    private fun loadStubData() {
        val defaultStatus = currentWeekDays.associateWith { HabitStatus.NONE }

        _weeklyHabits.value = listOf(
            WeeklyHabit(
                id = "habit1",
                title = "Read",
                weekStatus = defaultStatus.toMutableMap()
            ),
            WeeklyHabit(
                id = "habit2",
                title = "Exercise",
                weekStatus = defaultStatus.toMutableMap()
            ),
            WeeklyHabit(
                id = "habit3",
                title = "Sleep Early",
                weekStatus = defaultStatus.toMutableMap()
            )
        )
    }

    fun updateHabitStatus(habitId: String, day: DayOfWeek, status: HabitStatus) {
        _weeklyHabits.update { habitList ->
            habitList.map { habit ->
                if (habit.id == habitId) {
                    val updatedWeekStatus = habit.weekStatus.toMutableMap()
                    updatedWeekStatus[day] = status
                    habit.copy(weekStatus = updatedWeekStatus)
                } else habit
            }
        }
    }

    // Optionally: Clear or reset the week
    fun resetWeek() {
        val resetStatus = currentWeekDays.associateWith { HabitStatus.NONE }
        _weeklyHabits.update { list ->
            list.map { it.copy(weekStatus = resetStatus.toMutableMap()) }
        }
    }
}
