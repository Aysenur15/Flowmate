package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

enum class TimeRange {
    DAILY, WEEKLY, MONTHLY
}

data class HabitTimeSegment(
    val habitName: String,
    val minutes: Int,
    val color: Color
)

class TimerStatsViewModel : ViewModel() {

    private val _selectedRange = MutableStateFlow(TimeRange.DAILY)
    val selectedRange: StateFlow<TimeRange> = _selectedRange

    private val _timeSegments = MutableStateFlow<List<HabitTimeSegment>>(emptyList())
    val timeSegments: StateFlow<List<HabitTimeSegment>> = _timeSegments

    init {
        loadTimeSegments(TimeRange.DAILY)
    }

    fun onRangeSelected(range: TimeRange) {
        _selectedRange.value = range
        loadTimeSegments(range)
    }

    private fun loadTimeSegments(range: TimeRange) {
        viewModelScope.launch {
            val dummy = when (range) {
                TimeRange.DAILY -> listOf(
                    HabitTimeSegment("Reading", 30, Color(0xFF42A5F5)),
                    HabitTimeSegment("Walking", 45, Color(0xFF66BB6A)),
                    HabitTimeSegment("Meditating", 15, Color(0xFFFFA726))
                )
                TimeRange.WEEKLY -> listOf(
                    HabitTimeSegment("Reading", 180, Color(0xFF42A5F5)),
                    HabitTimeSegment("Walking", 240, Color(0xFF66BB6A)),
                    HabitTimeSegment("Meditating", 90, Color(0xFFFFA726))
                )
                TimeRange.MONTHLY -> listOf(
                    HabitTimeSegment("Reading", 600, Color(0xFF42A5F5)),
                    HabitTimeSegment("Walking", 720, Color(0xFF66BB6A)),
                    HabitTimeSegment("Meditating", 180, Color(0xFFFFA726))
                )
            }
            _timeSegments.value = dummy
        }
    }
}
