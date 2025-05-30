package com.flowmate.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        loadTimeSegments(TimeRange.WEEKLY, userId = "leJ77vgw5pYlCj0fawhOVwoTJqx1", date = today)
    }

    fun onRangeSelected(range: TimeRange) {
        _selectedRange.value = range
        val today = getTodayDate()
        loadTimeSegments(range, userId = "leJ77vgw5pYlCj0fawhOVwoTJqx1", date = today)
    }

   fun loadTimeSegments(range: TimeRange, userId: String, date: String) {
       db.collection("users")
           .document(userId)
           .collection("habits")
           .get()
           .addOnSuccessListener { habitsSnapshot ->
               val tempMap = mutableMapOf<String, Int>()
               val habits = habitsSnapshot.documents
               var fetched = 0

               if (habits.isEmpty()) {
                   _timeSegments.value = emptyList()
                   return@addOnSuccessListener
               }

               habits.forEachIndexed { i, habitDoc ->
                   val habitId = habitDoc.id
                   val habitName = habitDoc.getString("title") ?: "Unknown"

                   db.collection("users")
                       .document(userId)
                       .collection("habits")
                       .document(habitId)
                       .collection("habitTimes")
                       .get()
                       .addOnSuccessListener { timesSnapshot ->
                           val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                           val refDate = sdf.parse(date)
                           var totalMinutes = 0

                           timesSnapshot.documents.forEach { timeDoc ->
                               val docDateStr = timeDoc.getString("date") ?: return@forEach
                               val docDate = sdf.parse(docDateStr)
                               val minutes = timeDoc.getLong("minutes")?.toInt() ?: 0

                               val include = when (range) {
                                   TimeRange.DAILY -> docDateStr == date
                                   TimeRange.WEEKLY -> {
                                       if (docDate != null && refDate != null) {
                                           val diff = (refDate.time - docDate.time) / (1000 * 60 * 60 * 24)
                                           diff in 0..6
                                       } else false
                                   }
                                   TimeRange.MONTHLY -> {
                                       if (docDate != null && refDate != null) {
                                           val diff = (refDate.time - docDate.time) / (1000 * 60 * 60 * 24)
                                           diff in 0..29
                                       } else false
                                   }
                               }
                               if (include) totalMinutes += minutes
                           }

                           if (totalMinutes > 0) {
                               tempMap[habitName] = totalMinutes
                           }
                           fetched++
                           if (fetched == habits.size) {
                               val segments = tempMap.entries.mapIndexed { idx, entry ->
                                   HabitTimeSegment(
                                       habitName = entry.key,
                                       minutes = entry.value,
                                       color = colors[idx % colors.size]
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
