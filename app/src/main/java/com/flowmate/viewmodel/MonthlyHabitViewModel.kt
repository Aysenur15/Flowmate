package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.HabitStatus
import com.flowmate.ui.component.MonthlyHabit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MonthlyHabitViewModel(private val repository: HabitRepository, private val userId: String) : ViewModel() {

    private val currentMonth = YearMonth.now()
    private val daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }

    private val _monthlyHabits = MutableStateFlow<List<MonthlyHabit>>(emptyList())
    val monthlyHabits: StateFlow<List<MonthlyHabit>> = _monthlyHabits.asStateFlow()

    init {
        fetchHabitsFromFirestore()
    }

    private fun habitToMonthlyHabit(habit: Habit): MonthlyHabit {
        val monthStatus = daysInMonth.associateWith { day ->
            if (habit.completedDates
                    .map { java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate() }
                    .contains(day)) {
                HabitStatus.DONE
            } else {
                HabitStatus.NONE
            }
        }.toMutableMap()
        return MonthlyHabit(
            id = habit.id,
            title = habit.title,
            monthStatus = monthStatus
        )
    }

    fun fetchHabitsFromFirestore() {
        viewModelScope.launch {
            val habits = repository.getHabitsFromFirestore(userId)
            _monthlyHabits.value = habits.map { habitToMonthlyHabit(it) }
        }
    }

    fun updateHabitStatus(habitId: String, date: LocalDate, status: HabitStatus) {
        _monthlyHabits.update { habitList ->
            habitList.map { habit ->
                if (habit.id == habitId) {
                    val updated = habit.monthStatus.toMutableMap()
                    updated[date] = status
                    habit.copy(monthStatus = updated)
                } else habit
            }
        }
    }

    fun resetMonth() {
        val resetStatus = daysInMonth.associateWith { HabitStatus.NONE }.toMutableMap()
        _monthlyHabits.update { list ->
            list.map { it.copy(monthStatus = resetStatus.toMutableMap()) }
        }
    }
}
