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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.AddItemBottomSheet
import com.flowmate.ui.theme.CardShape
import com.flowmate.ui.theme.DoneColor
import com.flowmate.ui.theme.PendingColor
import com.flowmate.ui.theme.TaskCardBg

// 2) Data model for a task
data class TaskItem(
    val id: String,
    val title: String,
    val dueTime: String,          // e.g. "Today, 5:00 PM"
    val isCompleted: Boolean
)

// 3) The MyTasksScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    tasks: List<TaskItem>,
    onToggleTask: (String) -> Unit,
    onAddTask: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTask,
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
            items(tasks) { task ->
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
                        // Checkbox icon
                        IconButton(onClick = { onToggleTask(task.id) }) {
                            val icon =
                                if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.AccessTime
                            Icon(
                                imageVector = icon,
                                contentDescription = if (task.isCompleted) "Mark incomplete" else "Mark complete",
                                tint = if (task.isCompleted) DoneColor else PendingColor
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        // Title & due time
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = task.dueTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHabitsWithAdd(
    habits: List<Habit>,

    onAddHabit: (title: String) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { showAdd = false }
    ) { }
    AddItemBottomSheet(
        sheetTitle  = "New Habit",
        visible     = showAdd,
        onDismiss   = { showAdd = false },
        onAdd       = {
            if (newHabitName.isNotBlank()) {
                onAddHabit(newHabitName.trim())
                newHabitName = ""
            }
            showAdd = false
        },
        sheetContent = {
            OutlinedTextField(
                value = newHabitName,
                onValueChange = { newHabitName = it },
                label = { Text("Habit name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { showSheet ->
        // Your existing MyHabitsScreen body goes here…
        MyHabitScreen(
            habits           = habits,
            onToggleComplete = { /*…*/ },
            onAddHabit       = { showAdd = true },
            suggestions      = emptyList()
        )
    }
}
