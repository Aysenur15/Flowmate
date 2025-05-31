package com.flowmate.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.HabitStatus
import com.flowmate.ui.component.WeeklyHabit
import com.flowmate.ui.component.HabitType
import com.flowmate.worker.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WeeklyHabitViewModel(
    private val repository: HabitRepository,
    private val userId: String
) : ViewModel() {

    private val _weeklyHabits = MutableStateFlow<List<WeeklyHabit>>(emptyList())
    val weeklyHabits: StateFlow<List<WeeklyHabit>> = _weeklyHabits.asStateFlow()

    private val currentWeekDays = DayOfWeek.values().toList()

    // ⚠️ Context parametresi eklendi!
    fun fetchHabitsFromFirestore(context: Context) {
        viewModelScope.launch {
            val habits = repository.getHabitsFromFirestore(userId)
            val result = habits.map { habit ->
                if (habit.reminderEnabled && habit.reminderTime != null && habit.frequency.contains("week", ignoreCase = true)) {
                    try {
                        val time = LocalTime.parse(habit.reminderTime)
                        val frequency = habit.frequency.filter { it.isDigit() }.toIntOrNull() ?: 1
                        val randomDays = pickRandomWeekDays(frequency)
                        val dates = getDatesForThisWeek(randomDays)

                        dates.forEach { date ->
                            val dateTime = LocalDateTime.of(date, time)
                            ReminderScheduler.scheduleReminderIfEnabled(
                                context = context,
                                title = habit.title,
                                targetTime = dateTime,
                                isEnabled = true,
                                type = HabitType.WEEKLY,
                                time = time
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                habitToWeeklyHabit(habit)
            }

            _weeklyHabits.value = result
        }
    }


    private fun habitToWeeklyHabit(habit: Habit): WeeklyHabit {
        val completedDates = habit.completedDates.map {
            java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        }

        val weekStatus = currentWeekDays.associateWith { day ->
            val dateForDay = LocalDate.now().with(day)
            if (completedDates.contains(dateForDay)) HabitStatus.DONE else HabitStatus.NONE
        }.toMutableMap()

        return WeeklyHabit(
            id = habit.id,
            title = habit.title,
            weekStatus = weekStatus
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

    fun resetWeek() {
        val resetStatus = currentWeekDays.associateWith { HabitStatus.NONE }
        _weeklyHabits.update { list ->
            list.map { it.copy(weekStatus = resetStatus.toMutableMap()) }
        }
    }
    private fun pickRandomWeekDays(count: Int): List<DayOfWeek> {
        val allDays = DayOfWeek.values().toList()
        return allDays.shuffled().take(count.coerceAtMost(7))
    }

    private fun getDatesForThisWeek(days: List<DayOfWeek>): List<LocalDate> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        return days.map { startOfWeek.with(it) }
    }
}
