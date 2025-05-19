package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ReportsViewModel : ViewModel() {

    private val _weeklyHabitData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val weeklyHabitData: StateFlow<Map<String, Int>> = _weeklyHabitData

    init {
        loadWeeklyHabitCompletions()
    }

    private fun loadWeeklyHabitCompletions() {
        // Simulated weekly habit completion data
        viewModelScope.launch {
            _weeklyHabitData.value = mapOf(
                "Walk" to 5,
                "Read" to 2,
                "Meditate" to 3
            )
        }
    }
}