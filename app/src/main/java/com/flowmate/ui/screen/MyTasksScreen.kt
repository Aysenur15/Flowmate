package com.flowmate.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flowmate.repository.TaskRepository
import com.flowmate.ui.component.TaskItem
import com.flowmate.ui.theme.DoneColor
import com.flowmate.ui.theme.PendingColor
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
    var taskToDelete by remember { mutableStateOf<TaskItem?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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

                val context = LocalContext.current
                val datePickerDialog = remember {
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            newTaskDueTime = "%02d-%02d-%04d".format(dayOfMonth, month + 1, year)
                        },
                        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                        java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
                        java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
                    )
                }
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Select Due Date")
                }

                OutlinedTextField(
                    value = newTaskDueTime,
                    onValueChange = { newTaskDueTime = it },
                    label = { Text("Due Date") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePickerDialog.show()
                        },
                    readOnly = true
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
                                        taskRepository.getTasksFromFirestore(userId.toString())
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
                val isDark = isSystemInDarkTheme()
                val taskColorsLight = listOf(
                    Color(0xFFB39DDB), // mor
                    Color(0xFF80CBC4), // turkuaz
                    Color(0xFFFFAB91), // turuncu
                    Color(0xFFA5D6A7), // yeşil
                    Color(0xFFFFF59D), // sarı
                    Color(0xFF90CAF9), // mavi
                    Color(0xFFE6EE9C), // açık yeşil
                    Color(0xFFFFCC80), // açık turuncu
                    Color(0xFFF48FB1), // pembe
                    Color(0xFFB0BEC5)  // gri
                )
                val taskColorsDark = listOf(
                    Color(0xFF5E35B1), // koyu mor
                    Color(0xFF00897B), // koyu turkuaz
                    Color(0xFFF4511E), // koyu turuncu
                    Color(0xFF388E3C), // koyu yeşil
                    Color(0xFFFBC02D), // koyu sarı
                    Color(0xFF1976D2), // koyu mavi
                    Color(0xFF689F38), // koyu açık yeşil
                    Color(0xFFFFA000), // koyu açık turuncu
                    Color(0xFFD81B60), // koyu pembe
                    Color(0xFF455A64)  // koyu gri
                )
                val colorList = if (isDark) taskColorsDark else taskColorsLight
                val cardBg = colorList[task.id.hashCode().let { if (it < 0) -it else it } % colorList.size]
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
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
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isDark) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Due: ${task.dueTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
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
                                        taskRepository.getTasksFromFirestore(userId)
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

                        IconButton(onClick = {
                            taskToDelete = task
                            showDeleteConfirm = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Task",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm && taskToDelete != null) {
        ModalBottomSheet(
            onDismissRequest = { showDeleteConfirm = false },
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Are you sure you want to delete?", color = Color.Red)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null && taskToDelete != null) {
                            scope.launch {
                                taskRepository.deleteTaskFromFirestore(userId, taskToDelete!!.id)
                                taskList = taskRepository.getTasksFromFirestore(userId)
                                showDeleteConfirm = false
                                taskToDelete = null
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                        Text("Delete", color = Color.White)
                    }
                    OutlinedButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
