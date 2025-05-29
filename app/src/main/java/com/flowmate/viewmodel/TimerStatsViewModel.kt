package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class TimeRange {
    DAILY, WEEKLY, MONTHLY
}

data class HabitTimeSegment(
    val habitName: String,
    val minutes: Int,
    val color: Color
)

class TimerStatsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _selectedRange = MutableStateFlow(TimeRange.DAILY)
    val selectedRange: StateFlow<TimeRange> = _selectedRange

    private val _timeSegments = MutableStateFlow<List<HabitTimeSegment>>(emptyList())
    val timeSegments: StateFlow<List<HabitTimeSegment>> = _timeSegments

    // Colors to cycle through
    private val colors = listOf(
        Color(0xFF42A5F5), // Blue
        Color(0xFF66BB6A), // Green
        Color(0xFFFFA726)  // Orange
    )

    init {
        val today = getTodayDate()
        loadTimeSegments(TimeRange.DAILY, userId = "user_001", date = today)
    }

    fun onRangeSelected(range: TimeRange) {
        _selectedRange.value = range
        val today = getTodayDate()
        loadTimeSegments(range, userId = "user_001", date = today)
    }

    fun loadTimeSegments(range: TimeRange, userId: String, date: String) {
        db.collection("dailyHabitSummary")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val tempMap = mutableMapOf<String, Int>()

                val filteredDocs = snapshot.documents.filter { doc ->
                    val docDate = doc.getString("date") ?: return@filter false
                    when (range) {
                        TimeRange.DAILY -> docDate == date
                        TimeRange.WEEKLY -> isWithinPastDays(docDate, date, 7)
                        TimeRange.MONTHLY -> isWithinPastDays(docDate, date, 30)
                    }
                }

                val total = filteredDocs.size
                var fetched = 0

                if (total == 0) {
                    _timeSegments.value = emptyList()
                    return@addOnSuccessListener
                }

                filteredDocs.forEachIndexed { index, doc ->
                    val habitId = doc.getString("habitId") ?: return@forEachIndexed
                    val duration = doc.getLong("duration")?.toInt() ?: 0

                    db.collection("habits").document(habitId).get()
                        .addOnSuccessListener { habitDoc ->
                            val habitName = habitDoc.getString("title") ?: "Unknown"
                            tempMap[habitName] = tempMap.getOrDefault(habitName, 0) + duration / 60000
                            fetched++

                            if (fetched == total) {
                                val segments = tempMap.entries.mapIndexed { i, entry ->
                                    HabitTimeSegment(
                                        habitName = entry.key,
                                        minutes = entry.value,
                                        color = colors[i % colors.size]
                                    )
                                }
                                viewModelScope.launch {
                                    _timeSegments.emit(segments)
                                }
                            }
                        }
                }
            }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun isWithinPastDays(docDate: String, referenceDate: String, days: Int): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val doc = sdf.parse(docDate)
            val ref = sdf.parse(referenceDate)
            if (doc != null && ref != null) {
                val diff = (ref.time - doc.time) / (1000 * 60 * 60 * 24)
                diff in 0..(days - 1)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
