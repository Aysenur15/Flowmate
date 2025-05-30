package com.flowmate.viewmodel

import androidx.lifecycle.ViewModel
import com.flowmate.ui.component.TaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyTasksViewModal : ViewModel() {

    // stubbed tasks
    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    init {
        loadStubData()
    }

    private fun loadStubData() {
        // populate some dummy tasks
        _tasks.value = listOf(
            TaskItem("1", "Complete the report", "2023-10-01", false),
            TaskItem("2", "Prepare for the meeting", "2023-10-02", true),
        )
    }

    fun toggleTaskCompletion(taskId: String) {
        val updated = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else task
        }
        _tasks.value = updated
    }

    fun addTask(task: TaskItem) {
        _tasks.value = _tasks.value + task
    }
}