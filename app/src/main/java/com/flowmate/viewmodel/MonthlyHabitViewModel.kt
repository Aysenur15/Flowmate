package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import com.flowmate.ui.component.HabitStatus
import com.flowmate.ui.component.MonthlyHabit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth

class MonthlyHabitViewModel : ViewModel() {

    private val currentMonth = YearMonth.now()
    private val daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }

    private val _monthlyHabits = MutableStateFlow<List<MonthlyHabit>>(emptyList())
    val monthlyHabits: StateFlow<List<MonthlyHabit>> = _monthlyHabits.asStateFlow()

    init {
        loadStubData()
    }

    private fun loadStubData() {
        val defaultStatus = daysInMonth.associateWith { HabitStatus.NONE }.toMutableMap()

        _monthlyHabits.value = listOf(
            MonthlyHabit("habit1", "Read", defaultStatus.toMutableMap()),
            MonthlyHabit("habit2", "Exercise", defaultStatus.toMutableMap()),
            MonthlyHabit("habit3", "Sleep Early", defaultStatus.toMutableMap())
        )
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
