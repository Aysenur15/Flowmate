package com.flowmate.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.HabitType
import com.flowmate.worker.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.Year

// ViewModel for managing yearly habits and reminders
class YearlyHabitViewModel(
    private val repository: HabitRepository,
    private val userId: String
) : ViewModel() {

    private val _year = MutableStateFlow(Year.now().value)
    val year: StateFlow<Int> = _year

    private val _completedDays = MutableStateFlow<Set<LocalDate>>(emptySet())
    val completedDays: StateFlow<Set<LocalDate>> = _completedDays

    // Fetch habits from Firestore and schedule reminders
    fun fetchHabitsFromFirestore(context: Context) {
        viewModelScope.launch {
            val habits = repository.getHabitsFromFirestore(userId)
            val allCompletedDays = habits.flatMap { habit ->
                if (habit.reminderEnabled && habit.reminderTime != null && habit.frequency.contains("year", ignoreCase = true)) {
                    try {
                        val time = LocalTime.parse(habit.reminderTime)
                        val frequency = habit.frequency.filter { it.isDigit() }.toIntOrNull() ?: 1
                        val randomDays = pickRandomDaysInYear(frequency)

                        randomDays.forEach { date ->
                            val dateTime = LocalDateTime.of(date, time)
                            ReminderScheduler.scheduleReminderIfEnabled(
                                context = context,
                                title = habit.title,
                                targetTime = dateTime,
                                isEnabled = true,
                                type = HabitType.YEARLY,
                                time = time
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                habit.completedDates.map {
                    java.time.Instant.ofEpochMilli(it)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                }
            }.filter { it.year == _year.value }.toSet()

            _completedDays.value = allCompletedDays
        }
    }
    fun nextYear(context: Context) {
        _year.value += 1
        fetchHabitsFromFirestore(context)
    }

    fun previousYear(context: Context) {
        _year.value -= 1
        fetchHabitsFromFirestore(context)
    }
    // Pick random days in the current year for yearly habits
    private fun pickRandomDaysInYear(count: Int): List<LocalDate> {
        val year = LocalDate.now().year
        val daysInYear = (1..Year.of(year).length()).map { dayOfYear ->
            LocalDate.ofYearDay(year, dayOfYear)
        }
        return daysInYear.shuffled().take(count)
    }

}
