package com.flowmate.ui.component

import com.flowmate.data.UserEntity
import java.time.DayOfWeek
import java.time.LocalDate


// 2. Data models
data class Habit(
    val id: String,
    val title: String,
    val weeklyProgress: Float, // 0f..1f
    val isCompletedToday: Boolean,
    val hardnessLevel: Int,
    val frequency: String="",
    val reminderEnabled: Boolean=false,
    val reminderTime: String? = null
)

data class SmartSuggestion(
    val id: String,
    val text: String
)

// 2) Data model for a task
data class TaskItem(
    val id: String,
    val title: String,
    val dueTime: String,          // e.g. "Today, 5:00 PM"
    val isCompleted: Boolean,
    val reminderEnabled: Boolean=false,
    val reminderTime: String?=null,
)
data class WeeklyHabit(
    val id: String,
    val title: String,
    val weekStatus: MutableMap<DayOfWeek, HabitStatus>
)
enum class HabitStatus {
    DONE, SKIPPED, NONE
}
data class MonthlyHabit(
    val id: String,
    val title: String,
    val monthStatus: MutableMap<LocalDate, HabitStatus>
)
data class YearlyHabit(
    val id: String,
    val title: String,
    val completedDays: MutableSet<LocalDate>
)

data class AuthState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val currentUser: UserEntity? = null,
    val userName: String? = null
)

data class AiSuggestion(
    val icon: String,
    val title: String,
    val message: String
)



