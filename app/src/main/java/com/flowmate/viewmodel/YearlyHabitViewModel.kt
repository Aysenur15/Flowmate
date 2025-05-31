package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year

class YearlyHabitViewModel(private val repository: HabitRepository, private val userId: String) : ViewModel() {
    private val _year = MutableStateFlow(Year.now().value)
    val year: StateFlow<Int> = _year

    private val _completedDays = MutableStateFlow<Set<LocalDate>>(emptySet())
    val completedDays: StateFlow<Set<LocalDate>> = _completedDays

    init {
        fetchHabitsFromFirestore()
    }

    fun fetchHabitsFromFirestore() {
        viewModelScope.launch {
            val habits = repository.getHabitsFromFirestore(userId)
            val allCompletedDays = habits.flatMap { habit ->
                habit.completedDates.map { java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate() }
            }.filter { it.year == _year.value }.toSet()
            _completedDays.value = allCompletedDays
        }
    }

    fun nextYear() {
        _year.value += 1
        fetchHabitsFromFirestore()
    }

    fun previousYear() {
        _year.value -= 1
        fetchHabitsFromFirestore()
    }
}
