package com.flowmate.ui.component

// 2. Data models
data class Habit(
    val id: String,
    val title: String,
    val weeklyProgress: Float, // 0f..1f
    val isCompletedToday: Boolean
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
    val isCompleted: Boolean
)