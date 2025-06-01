package com.flowmate.repository

import com.flowmate.ui.component.TaskItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepository @Inject constructor() {

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: Flow<List<TaskItem>> = _tasks

    private val firestore = FirebaseFirestore.getInstance()

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

    suspend fun addTaskToFirestore(userId: String, task: TaskItem) {
        val taskMap = hashMapOf(
            "taskId" to task.id,
            "userId" to userId,
            "title" to task.title,
            "deadline" to task.dueTime, // string ise Firestore'da string olarak saklanır
            "priority" to (0),
            "isCompleted" to task.isCompleted,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .document(task.id)
            .set(taskMap)
            .await()
    }

    suspend fun getTasksFromFirestore(userId: String): List<TaskItem> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            TaskItem(
                id = doc.getString("taskId") ?: doc.id,
                title = doc.getString("title") ?: "",
                dueTime = doc.getString("deadline") ?: "",
                isCompleted = doc.getBoolean("isCompleted") ?: false,
                reminderEnabled = false, // Firestore'dan çekmek isterseniz ekleyin
                reminderTime = null // Firestore'dan çekmek isterseniz ekleyin
            )
        }
    }

    suspend fun updateTaskCompletionInFirestore(userId: String, taskId: String, isCompleted: Boolean) {
        val taskRef = firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .document(taskId)
        taskRef.update("isCompleted", isCompleted).await()
    }

    suspend fun deleteTaskFromFirestore(userId: String, taskId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .document(taskId)
            .delete()
            .await()
    }

}
