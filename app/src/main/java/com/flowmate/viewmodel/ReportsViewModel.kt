package com.flowmate.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import com.flowmate.ui.component.DifficultyCounts


data class Habit(
    val difficultyLevel: Int,
    val completedDates: List<Long>
)

class ReportsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _weeklyHabitData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val weeklyHabitData: StateFlow<Map<String, Int>> = _weeklyHabitData

    private val _habitTimeSegments = MutableStateFlow<List<HabitTimeSegment>>(emptyList())
    val habitTimeSegments: StateFlow<List<HabitTimeSegment>> = _habitTimeSegments

    private val _habitConsistency = MutableStateFlow<List<Pair<String, List<Boolean>>>>(emptyList())
    val habitConsistency: StateFlow<List<Pair<String, List<Boolean>>>> = _habitConsistency

    private val _difficultyRawData = MutableStateFlow<List<List<Int>>>(emptyList())
    val difficultyRawData: StateFlow<List<List<Int>>> = _difficultyRawData

    private val colors = listOf(
        Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFA726),
        Color(0xFFAB47BC), Color(0xFFFF7043), Color(0xFF26C6DA), Color(0xFFD4E157)
    )

    private val MILLIS_PER_MINUTE = 60000

    fun fetchHabitDurationsForDate(userId: String, date: String) {
        val resultMap = mutableMapOf<String, Int>()

        db.collection("dailyHabitSummary")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { snapshot ->
                val total = snapshot.size()
                var fetched = 0

                if (total == 0) {
                    viewModelScope.launch { _weeklyHabitData.emit(emptyMap()) }
                    return@addOnSuccessListener
                }

                snapshot.documents.forEach { doc ->
                    val habitId = doc.getString("habitId") ?: return@forEach
                    val duration = doc.getLong("duration")?.toInt() ?: 0

                    db.collection("habits").document(habitId).get()
                        .addOnSuccessListener { habitDoc ->
                            val habitName = habitDoc.getString("title") ?: "Unknown"
                            resultMap[habitName] = resultMap.getOrDefault(habitName, 0) + duration
                            fetched++
                            if (fetched == total) {
                                viewModelScope.launch { _weeklyHabitData.emit(resultMap) }
                            }
                        }
                }
            }
    }

    fun fetchHabitTimeSegments(userId: String, date: String) {
        val tempMap = mutableMapOf<String, Int>()

        db.collection("dailyHabitSummary")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { snapshot ->
                val total = snapshot.size()
                var fetched = 0

                if (total == 0) {
                    viewModelScope.launch { _habitTimeSegments.emit(emptyList()) }
                    return@addOnSuccessListener
                }

                snapshot.documents.forEach { doc ->
                    val habitId = doc.getString("habitId") ?: return@forEach
                    val duration = doc.getLong("duration")?.toInt() ?: 0

                    db.collection("habits").document(habitId).get()
                        .addOnSuccessListener { habitDoc ->
                            val habitName = habitDoc.getString("title") ?: "Unknown"
                            tempMap[habitName] =
                                tempMap.getOrDefault(habitName, 0) + duration / MILLIS_PER_MINUTE
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
                                    _habitTimeSegments.emit(segments)
                                }
                            }
                        }
                }
            }
    }

    fun fetchHabitConsistencyFromCompletedDates(userId: String) {
        val utc = java.util.TimeZone.getTimeZone("UTC")
        val today = Calendar.getInstance(utc).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val past7Midnights = (0..6).map {
            Calendar.getInstance().apply {
                timeInMillis = today.timeInMillis
                add(Calendar.DAY_OF_YEAR, -it)
            }.timeInMillis
        }.reversed()

        db.collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .addOnSuccessListener { snapshot ->
                val result = snapshot.documents.map { doc ->
                    val habitName = doc.getString("title") ?: "Unknown"
                    val completedDates = (doc.get("completedDates") as? List<*>)?.mapNotNull {
                        when (it) {
                            is Long -> it
                            is Double -> it.toLong()
                            is Number -> it.toLong()
                            else -> null
                        }
                    } ?: emptyList()

                    val weeklyStatus = past7Midnights.map { midnight ->
                        completedDates.any { completed ->
                            Calendar.getInstance().apply {
                                timeInMillis = completed
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis == midnight
                        }
                    }

                    habitName to weeklyStatus
                }

                viewModelScope.launch { _habitConsistency.emit(result) }
            }
    }

    fun fetchDifficultyDataFromFirestore(userId: String) {

        db.collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .addOnSuccessListener { snapshot ->
                val habits = snapshot.documents.mapNotNull { doc ->
                    val level = (doc.get("difficultyLevel") as? Long)?.toInt()
                    val dates = doc.get("completedDates") as? List<Long>

                    if (level != null && dates != null) {
                        Habit(level, dates)
                    } else {
                        null
                    }
                }

                val grouped = groupHabitsByDay(habits)

                _difficultyRawData.value = grouped
            }
            .addOnFailureListener {
                _difficultyRawData.value = emptyList()
            }
    }


    private fun groupHabitsByDay(habits: List<Habit>): List<List<Int>> {
        val today = LocalDate.now()
        val last7Days = (0..6).map { today.minusDays((6 - it).toLong()) }
        val grouped = mutableMapOf<LocalDate, MutableList<Int>>()
        last7Days.forEach { grouped[it] = mutableListOf() }

        for (habit in habits) {
            for (timestamp in habit.completedDates) {
                val localDate = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                if (localDate in grouped) {
                    grouped[localDate]?.add(habit.difficultyLevel)
                }
            }
        }

        val result = last7Days.map { grouped[it] ?: emptyList() }
        return result
    }

}
