package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.HabitStatus
import com.flowmate.ui.component.WeeklyHabit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class WeeklyHabitViewModel(private val repository: HabitRepository, private val userId: String) : ViewModel() {

    private val _weeklyHabits = MutableStateFlow<List<WeeklyHabit>>(emptyList())
    val weeklyHabits: StateFlow<List<WeeklyHabit>> = _weeklyHabits.asStateFlow()

    // Get current week range (Mon to Sun)
    private val currentWeekDays = DayOfWeek.values().toList()

    init {
        fetchHabitsFromFirestore()
    }

    private fun habitToWeeklyHabit(habit: Habit): WeeklyHabit {
        // Haftalık statüleri oluşturmak için örnek bir dönüşüm
        val weekStatus = DayOfWeek.values().associateWith { day ->
            if (habit.completedDates
                    .map { java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate() }
                    .contains(LocalDate.now().with(day))) {
                HabitStatus.DONE
            } else {
                HabitStatus.NONE
            }
        }.toMutableMap()
        return WeeklyHabit(
            id = habit.id,
            title = habit.title,
            weekStatus = weekStatus
        )
    }

    fun fetchHabitsFromFirestore() {
        viewModelScope.launch {
            val habits = repository.getHabitsFromFirestore(userId)
            _weeklyHabits.value = habits.map { habitToWeeklyHabit(it) }
        }
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
