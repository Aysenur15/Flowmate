package com.flowmate.ui.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flowmate.data.UserEntity
import com.flowmate.repository.HabitRepository
import com.flowmate.viewmodel.MonthlyHabitViewModel
import com.flowmate.viewmodel.WeeklyHabitViewModel
import com.flowmate.viewmodel.YearlyHabitViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime


// 2. Data models
data class Habit(
    val id: String,
    val title: String,
    val weeklyProgress: Float,
    val isCompletedToday: Boolean,
    val hardnessLevel: Int,
    val frequency: String = "",
    val reminderEnabled: Boolean = false,
    val reminderTime: String? = null,
    val completedDates: List<Long> = emptyList(),
    val userId: String = "",

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
    val weekStatus: MutableMap<DayOfWeek, HabitStatus>,
    val reminderTime: LocalTime? = null,
    val reminderEnabled: Boolean = false,
    val frequency: String = ""
)
enum class HabitStatus {
    DONE, SKIPPED, NONE
}
enum class HabitType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class MonthlyHabit(
    val id: String,
    val title: String,
    val monthStatus: MutableMap<LocalDate, HabitStatus>,
    val reminderTime: LocalTime? = null,
    val reminderEnabled: Boolean = false,
    val frequency: String = ""
)
data class YearlyHabit(
    val id: String,
    val title: String,
    val completedDays: MutableSet<LocalDate>,
    val reminderTime: LocalTime? = null,
    val reminderEnabled: Boolean = false,
    val frequency: String = ""
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
data class DifficultyCounts(
    val easy: Int,
    val medium: Int,
    val hard: Int
)

class WeeklyHabitViewModelFactory(
    private val repository: HabitRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeeklyHabitViewModel(repository, userId) as T
    }
}

class MonthlyHabitViewModelFactory(
    private val repository: HabitRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MonthlyHabitViewModel(repository, userId) as T
    }
}

class YearlyHabitViewModelFactory(
    private val repository: HabitRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return YearlyHabitViewModel(repository, userId) as T
    }
}



