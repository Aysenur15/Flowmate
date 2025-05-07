package com.flowmate.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flowmate.ui.theme.FlowMateTheme

@Composable
fun AddTaskScreen(
    onAdd: (TaskItem) -> Unit, // Callback to add the task
    modifier: Modifier = Modifier
) {
    var taskName by remember { mutableStateOf("") }
    var hardnessLevel by remember { mutableStateOf(5) }
    var deadline by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Task",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Task Name + Icon
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Name + Icon") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Hardness Level
        OutlinedTextField(
            value = hardnessLevel.toString(),
            onValueChange = { hardnessLevel = it.toIntOrNull() ?: 5 },
            label = { Text("Hardness Level $hardnessLevel") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Deadline
        OutlinedTextField(
            value = deadline,
            onValueChange = { deadline = it },
            label = { Text("Deadline") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Reminder Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reminder", modifier = Modifier.weight(1f))
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Add Button
        Button(
            onClick = {
                if (taskName.isNotEmpty() && deadline.isNotEmpty()) {
                    val newTask = TaskItem(
                        id = "task_${System.currentTimeMillis()}",
                        title = taskName,
                        dueTime = deadline,
                        isCompleted = false
                    )
                    onAdd(newTask)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = taskName.isNotEmpty() && deadline.isNotEmpty()
        ) {
            Text(text = "Add")
        }
    }
}

