package com.flowmate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flowmate.repository.TaskRepository
import com.flowmate.ui.component.TaskItem
import com.flowmate.ui.theme.CardShape
import com.flowmate.ui.theme.DoneColor
import com.flowmate.ui.theme.PendingColor
import com.flowmate.ui.theme.TaskCardBg
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    tasks: List<TaskItem>,
    onToggleTask: (String) -> Unit,
    onAddTask: (TaskItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDueTime by remember { mutableStateOf("") }
    var hardnessLevel by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(false) }
    var taskRepository = remember { TaskRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var taskList: List<TaskItem> by remember { mutableStateOf<List<TaskItem>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            taskList = taskRepository.getTasksFromFirestore(userId)
        }
    }



    if (sheetState.isVisible) {
        var reminderTime by remember { mutableStateOf("") }

        ModalBottomSheet(
            onDismissRequest = { scope.launch { sheetState.hide() } },
            sheetState = sheetState,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("New Task", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = newTaskDueTime,
                    onValueChange = { newTaskDueTime = it },
                    label = { Text("Due Time") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = hardnessLevel,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() } && (it.toIntOrNull()
                                ?: 0) in 1..5 || it.isBlank()) {
                            hardnessLevel = it
                        }
                    },
                    label = { Text("Hardness level (1-5)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reminder",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    androidx.compose.material3.Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }

                if (reminderEnabled) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("Reminder Time (e.g., 09:00 AM)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            newTaskTitle = ""
                            newTaskDueTime = ""
                            reminderEnabled = false
                            reminderTime = ""
                            scope.launch { sheetState.hide() }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank() && newTaskDueTime.isNotBlank()) {
                                val newTask = TaskItem(
                                    id = System.currentTimeMillis().toString(),
                                    title = newTaskTitle.trim(),
                                    dueTime = newTaskDueTime.trim(),
                                    isCompleted = false,
                                    reminderEnabled = reminderEnabled,
                                    reminderTime = if (reminderEnabled) reminderTime else null
                                )
                                scope.launch {
                                    try {
                                        taskRepository.addTaskToFirestore(userId = userId.toString(), task = newTask)
                                    }
                                    catch ( e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                onAddTask(newTask)

                                newTaskTitle = ""
                                newTaskDueTime = ""
                                reminderEnabled = false
                                reminderTime = ""
                            }
                            scope.launch { sheetState.hide() }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { scope.launch { sheetState.show() } },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                text = { Text("New") }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(taskList) { task ->
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = TaskCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Due: ${task.dueTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            if (task.reminderEnabled && !task.reminderTime.isNullOrBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Reminder: ${task.reminderTime}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4CAF50) // green-ish
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (userId != null) {
                                        taskRepository.updateTaskCompletionInFirestore(
                                            userId = userId,
                                            taskId = task.id,
                                            isCompleted = !task.isCompleted
                                        )
                                    }
                                }
                            },
                            enabled = !task.isCompleted
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                                contentDescription = if (task.isCompleted) "Tamamlandı" else "Tamamlanmadı",
                                tint = if (task.isCompleted) DoneColor else PendingColor
                            )
                        }
                    }
                }
            }
        }
    }
}
