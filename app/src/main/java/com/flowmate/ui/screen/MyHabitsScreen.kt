package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.NavController
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.SmartSuggestion
import com.flowmate.ui.theme.HabitCardBg
import com.flowmate.ui.theme.HabitProgressColor
import com.flowmate.ui.theme.TickColor
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


// 3. The MyHabitsScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHabitsScreen(
    habits: List<Habit>,
    suggestions: List<SmartSuggestion>,
    onToggleComplete: (habitId: String) -> Unit,
    onAddHabit: () -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Habits", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50)),
                actions = {
                    IconButton(onClick = { /* TODO: Open settings */ }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddHabit() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Habit") },
                text = { Text("New") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (suggestions.isNotEmpty()) {
                Text(
                    text = "Suggestions for you",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(suggestions) { suggestion ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(width = 200.dp, height = 100.dp)
                        ) {
                            Box(Modifier.padding(12.dp)) {
                                Text(
                                    text = suggestion.text,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits) { habit ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = HabitCardBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { habit.weeklyProgress },
                                    modifier = Modifier.size(48.dp),
                                    color = HabitProgressColor,
                                    strokeWidth = 6.dp,
                                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                                )
                                Text(
                                    text = "${(habit.weeklyProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = habit.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            IconButton(onClick = { onToggleComplete(habit.id) }) {
                                val icon = if (habit.isCompletedToday)
                                    Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Toggle Complete",
                                    tint = TickColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("habitProgress") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("View Progress")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHabitsWithModalSheet(
    habits: List<Habit>,
    suggestions: List<SmartSuggestion>,
    onToggleComplete: (String) -> Unit,
    onAddHabit: (Habit) -> Unit,
    navController: NavController
) {
    val habitRepository = remember { HabitRepository() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var habitList by remember { mutableStateOf<List<Habit>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            habitList = habitRepository.getHabitsFromFirestore(userId)
        }
    }

    val onToggleCompleteHandler: (String) -> Unit = { habitId ->
        if (userId != null) {
            scope.launch {
                habitRepository.markHabitCompletedForToday(userId, habitId)
                habitList = habitRepository.getHabitsFromFirestore(userId)
            }
        }
    }

    var newHabitName by remember { mutableStateOf("") }
    var hardnessLevel by remember { mutableStateOf("") }
    var frequencyCount by remember { mutableStateOf("") }
    var frequencyPeriod by remember { mutableStateOf("") }
    var periodDropdownExpanded by remember { mutableStateOf(false) }
    val periodOptions = listOf("day", "week", "month", "year")
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("") }

    if (sheetState.isVisible) {
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
                Text("New Habit", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text("Habit name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = hardnessLevel,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() } && (it.toIntOrNull() ?: 0) in 1..5 || it.isBlank()) {
                            hardnessLevel = it
                        }
                    },
                    label = { Text("Hardness level (1-5)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = frequencyCount,
                    onValueChange = {
                        val value = it.toIntOrNull()
                        if (value in 1..30 || it.isBlank()) {
                            frequencyCount = it
                        }
                    },
                    label = { Text("How many times?") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = periodDropdownExpanded,
                    onExpandedChange = { periodDropdownExpanded = !periodDropdownExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = frequencyPeriod,
                        onValueChange = {},
                        label = { Text("Period") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = periodDropdownExpanded,
                        onDismissRequest = { periodDropdownExpanded = false }
                    ) {
                        periodOptions.forEach { period ->
                            val isSelected = frequencyPeriod == period
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        period,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                    )
                                },
                                onClick = {
                                    frequencyPeriod = period
                                    periodDropdownExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
                                    )
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reminder", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }

                if (reminderEnabled) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("Reminder Time (e.g., 08:00 AM)") },
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
                            newHabitName = ""
                            hardnessLevel = ""
                            frequencyCount = ""
                            frequencyPeriod = "day"
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
                            if (newHabitName.isNotBlank() && userId != null) {
                                val frequency = if (frequencyCount.isNotBlank())
                                    "$frequencyCount per $frequencyPeriod"
                                else ""

                                val habit = Habit(
                                    id = System.currentTimeMillis().toString(),
                                    title = newHabitName.trim(),
                                    weeklyProgress = 0f,
                                    isCompletedToday = false,
                                    hardnessLevel = hardnessLevel.toIntOrNull() ?: 1,
                                    frequency = frequency,
                                    reminderEnabled = reminderEnabled,
                                    reminderTime = if (reminderEnabled) reminderTime else null
                                )

                                scope.launch {
                                    try {
                                        habitRepository.addHabitToFirestore(userId, habit)
                                    } catch (e: Exception) {
                                        // Hata yönetimi: Snackbar, Toast, log, vs. eklenebilir
                                    }
                                    onAddHabit(habit)
                                    newHabitName = ""
                                    hardnessLevel = ""
                                    frequencyCount = ""
                                    frequencyPeriod = "day"
                                    reminderEnabled = false
                                    reminderTime = ""
                                    sheetState.hide()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }

    MyHabitsScreen(
        habits = habitList,
        suggestions = suggestions,
        onToggleComplete = onToggleCompleteHandler,
        onAddHabit = { scope.launch { sheetState.show() } },
        navController = navController
    )

}
