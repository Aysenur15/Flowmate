package com.flowmate.repository

import com.flowmate.ui.component.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TaskRepository @Inject constructor() {

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: Flow<List<TaskItem>> = _tasks

    // Stub method to simulate task loading
    init {
        loadStubTasks()
    }

    private fun loadStubTasks() {
        _tasks.value = listOf(
            TaskItem("1", "Finish project proposal", "Today, 3:00 PM", isCompleted = false),
            TaskItem("2", "Grocery shopping", "Tomorrow, 10:00 AM", isCompleted = true),
            TaskItem("3", "Call Alice", "Today, 6:30 PM", isCompleted = false)
        )
    }

    // Toggle task completion
    fun toggleTaskCompletion(taskId: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else task
        }
    }

    // Add a new task
    fun addTask(task: TaskItem) {
        _tasks.value += task
    }

}


