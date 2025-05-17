package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import com.flowmate.ui.component.HabitStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

class YearlyHabitViewModel : ViewModel() {
    private val _year = MutableStateFlow(Year.now().value)
    val year: StateFlow<Int> = _year

    private val _completedDays = MutableStateFlow<Set<LocalDate>>(generateRandomHabits(_year.value))
    val completedDays: StateFlow<Set<LocalDate>> = _completedDays

    fun nextYear() {
        _year.value += 1
        _completedDays.value = generateRandomHabits(_year.value)
    }

    fun previousYear() {
        _year.value -= 1
        _completedDays.value = generateRandomHabits(_year.value)
    }

    companion object {
        private fun generateRandomHabits(year: Int): Set<LocalDate> {
            // Dummy data for testing – randomly generate 100 completed days
            return (1..100).map {
                val month = (1..12).random()
                val day = (1..YearMonth.of(year, month).lengthOfMonth()).random()
                LocalDate.of(year, month, day)
            }.toSet()
        }
    }
}
