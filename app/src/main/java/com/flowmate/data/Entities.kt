package com.flowmate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val email: String,
    val themePreference: String,
    val createdAt: Long,
    val passwordHash: String
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val habitId: String,
    val userId: String,
    val title: String,
    val recurrence: String, // daily, weekly, monthly
    val reminderTime: Long?,
    val createdAt: Long,
    val completedDates: List<Long>
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val taskId: String,
    val userId: String,
    val title: String,
    val deadline: Long,
    val priority: Int,
    val isCompleted: Boolean,
    val createdAt: Long
)

@Entity(tableName = "timer_logs")
data class TimerLogEntity(
    @PrimaryKey val logId: String,
    val userId: String,
    val habitId: String?,
    val taskId: String?,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val moodNote: String?
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val reminderId: String,
    val userId: String,
    val habitId: String?,
    val taskId: String?,
    val reminderTime: Long,
    val isActive: Boolean
)

@Entity(tableName = "suggestions")
data class SuggestionEntity(
    @PrimaryKey val suggestionId: String,
    val userId: String,
    val suggestionText: String,
    val createdAt: Long
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val settingsId: String,
    val userId: String,
    val themePreference: String,
    val notificationEnabled: Boolean,
    val languagePreference: String
)

@Entity(tableName = "analytics")
data class AnalyticsEntity(
    @PrimaryKey val analyticsId: String,
    val userId: String,
    val habitId: String?,
    val taskId: String?,
    val date: Long,
    val duration: Long,
    val completionStatus: Boolean
)

@Entity(tableName = "habit_reminders")
data class HabitReminderCrossRef(
    val habitId: String,
    val reminderId: String
)
@Entity(tableName = "task_reminders")
data class TaskReminderCrossRef(
    val taskId: String,
    val reminderId: String
)
@Entity(tableName = "habit_timer_logs")
data class HabitTimerLogCrossRef(
    val habitId: String,
    val timerLogId: String
)

@Entity(tableName = "task_timer_logs")
data class TaskTimerLogCrossRef(
    val taskId: String,
    val timerLogId: String
)
@Entity(tableName = "habit_suggestions")
data class HabitSuggestionCrossRef(
    val habitId: String,
    val suggestionId: String
)
@Entity(tableName = "task_suggestions")
data class TaskSuggestionCrossRef(
    val taskId: String,
    val suggestionId: String
)
@Entity(tableName = "habit_analytics")
data class HabitAnalyticsCrossRef(
    val habitId: String,
    val analyticsId: String
)
@Entity(tableName = "task_analytics")
data class TaskAnalyticsCrossRef(
    val taskId: String,
    val analyticsId: String
)

@Entity(tableName = "habit_settings")
data class HabitSettingsCrossRef(
    val habitId: String,
    val settingsId: String
)
@Entity(tableName = "task_settings")
data class TaskSettingsCrossRef(
    val taskId: String,
    val settingsId: String
)


