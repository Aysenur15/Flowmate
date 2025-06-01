package com.flowmate.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.HabitStatus
import com.flowmate.ui.component.MonthlyHabit
import com.flowmate.ui.component.HabitType
import com.flowmate.worker.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime

// ViewModel for managing monthly habits and reminders
class MonthlyHabitViewModel(
    private val repository: HabitRepository,
    private val userId: String
) : ViewModel() {

    private val currentMonth = YearMonth.now()
    private val daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }

    private val _monthlyHabits = MutableStateFlow<List<MonthlyHabit>>(emptyList())
    val monthlyHabits: StateFlow<List<MonthlyHabit>> = _monthlyHabits.asStateFlow()

    private fun habitToMonthlyHabit(habit: Habit): MonthlyHabit {
        val completedDates = habit.completedDates.map {
            java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        }

        val monthStatus = daysInMonth.associateWith { day ->
            if (completedDates.contains(day)) HabitStatus.DONE else HabitStatus.NONE
        }.toMutableMap()

        return MonthlyHabit(
            id = habit.id,
            title = habit.title,
            monthStatus = monthStatus
        )
    }

    fun fetchHabitsFromFirestore(context: Context) {
        viewModelScope.launch {
            val habits = repository.getHabitsFromFirestore(userId)
            val result = habits.map { habit ->
                if (habit.reminderEnabled && habit.reminderTime != null && habit.frequency.contains("month", ignoreCase = true)) {
                    try {
                        val time = LocalTime.parse(habit.reminderTime)
                        val frequency = habit.frequency.filter { it.isDigit() }.toIntOrNull() ?: 1
                        val randomDays = pickRandomDaysInMonth(frequency)

                        randomDays.forEach { date ->
                            val dateTime = LocalDateTime.of(date, time)
                            ReminderScheduler.scheduleReminderIfEnabled(
                                context = context,
                                title = habit.title,
                                targetTime = dateTime,
                                isEnabled = true,
                                type = HabitType.MONTHLY,
                                time = time
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                habitToMonthlyHabit(habit)
            }

            _monthlyHabits.value = result
        }
    }

    //Update status of a habit for a specific date
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

    // Reset the status of all habits for the current month
    fun resetMonth() {
        val resetStatus = daysInMonth.associateWith { HabitStatus.NONE }.toMutableMap()
        _monthlyHabits.update { list ->
            list.map { it.copy(monthStatus = resetStatus.toMutableMap()) }
        }
    }
    // Reset the status of a specific habit for the current month
    private fun pickRandomDaysInMonth(count: Int): List<LocalDate> {
        val today = LocalDate.now()
        val yearMonth = YearMonth.of(today.year, today.month)
        val days = (1..yearMonth.lengthOfMonth()).map { day -> yearMonth.atDay(day) }
        return days.shuffled().take(count)
    }

}
