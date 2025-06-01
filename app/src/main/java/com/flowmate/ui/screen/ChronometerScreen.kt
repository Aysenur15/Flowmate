package com.flowmate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.Habit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronometerScreen(
    habitList: List<Habit> = emptyList(),
    userId: String
) {
    // Stopwatch state
    var startTime by remember { mutableLongStateOf(0L) }
    var accumulated by remember { mutableLongStateOf(0L) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var laps by remember { mutableStateOf(listOf<Long>()) }
    var showHabitDialog by remember { mutableStateOf(false) }
    var selectedHabitId by remember { mutableStateOf<String?>(null) }
    var moodNote by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Ticker when running
    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = System.currentTimeMillis()
            while (isRunning) {
                elapsed = System.currentTimeMillis() - startTime + accumulated
                delay(1L)
            }
            accumulated = elapsed
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer display
            Text(
                text = formatTime(elapsed),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )

            // Controls: Start/Pause, Lap, Reset
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()

            ) {
                // Start / Pause button
                Button(
                    onClick = {
                        if (isRunning) {
                            isRunning = false
                            showHabitDialog = true // Kronometre durunca dialog aç
                        } else {
                            isRunning = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isRunning) "Pause" else "Start")
                }

                // Lap button (only when running)
                OutlinedButton(
                    onClick = { laps = listOf(elapsed) + laps },
                    enabled = isRunning,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Lap")
                    Spacer(Modifier.width(8.dp))
                    Text("Lap")
                }

                // Reset button (only when stopped and time > 0)
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        accumulated = 0L
                        elapsed = 0L
                        laps = emptyList()
                    },
                    enabled = !isRunning && accumulated > 0L,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
                    Spacer(Modifier.width(8.dp))
                    Text("Reset")
                }
            }

            // Laps list
            if (laps.isNotEmpty()) {
                Text(
                    "Laps",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(laps) { index, lapTime ->
                        Text(
                            text = "Lap ${laps.size - index}: ${formatTime(lapTime)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Habit seçme ve log kaydetme dialogu
            if (showHabitDialog) {
                AlertDialog(
                    onDismissRequest = { showHabitDialog = false },
                    title = { Text("Which Habit do you want to add this duration to?") },
                    text = {
                        Column {
                            var expanded by remember { mutableStateOf(false) }
                            OutlinedButton(onClick = { expanded = true }) {
                                Text(habitList.find { it.id == selectedHabitId }?.title ?: "Choose Habit")
                            }
                            androidx.compose.material3.DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                habitList.forEach { habit ->
                                    DropdownMenuItem(
                                        text = { Text(habit.title) },
                                        onClick = {
                                            selectedHabitId = habit.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = moodNote,
                                onValueChange = { moodNote = it },
                                label = { Text("Mood/Note (Optional)") },
                                singleLine = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (selectedHabitId != null) {
                                    val log = com.flowmate.data.TimerLogEntity(
                                        logId = UUID.randomUUID().toString(),
                                        userId = userId,
                                        habitId = selectedHabitId,
                                        taskId = null,
                                        startTime = startTime,
                                        endTime = startTime + elapsed,
                                        duration = elapsed,
                                        moodNote = moodNote.takeIf { it.isNotBlank() }
                                    )
                                    scope.launch {
                                        com.flowmate.repository.HabitRepository().addTimerLogToFirestore(log)
                                        showHabitDialog = false
                                        selectedHabitId = null
                                        moodNote = ""
                                    }
                                }
                            }
                        ) { Text("Save") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showHabitDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun formatTime(ms: Long): String {
    val centis = (ms / 10) % 100
    val seconds = (ms / 1_000) % 60
    val minutes = (ms / 60_000) % 60
    val hours = ms / 3_600_000
    return buildString {
        if (hours > 0) {
            append("${hours}:")
            append("%02d".format(minutes))
        } else {
            append("%02d".format(minutes))
        }
        append(":")
        append("%02d".format(seconds))
        append(":")
        append("%02d".format(centis))
    }
}

