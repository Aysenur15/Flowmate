package com.flowmate.ui.screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.SmartSuggestion
import com.flowmate.ui.theme.HabitCardBg
import com.flowmate.ui.theme.HabitProgressColor
import com.flowmate.ui.theme.TickColor
import kotlinx.coroutines.launch

// 3. The MyHabitsScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHabitsScreen(
    habits: List<Habit>,
    suggestions: List<SmartSuggestion>,
    onToggleComplete: (habitId: String) -> Unit,
    onAddHabit: () -> Unit
) {

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
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
                onClick = {
                    // Open the modal sheet
                    onAddHabit()
                },
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
            // AI suggestions strip
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

            // Habits list
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
                            // Progress circle
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
                            // Title
                            Text(
                                text = habit.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            // Tick button
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHabitsWithModalSheet(
    habits: List<Habit>,
    suggestions: List<SmartSuggestion>,
    onToggleComplete: (String) -> Unit,
    onAddHabit: (String) -> Unit
) {
    // 1️⃣ Sheet state & coroutine scope
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        /*confirmValueChange = { it != SheetValue.Expanded },*/
    )
    val scope = rememberCoroutineScope()

    // 2️⃣ Form state inside sheet
    var newHabitName by remember { mutableStateOf("") }

    // 3️⃣ The sheet itself
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

                // Input field
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text("Habit name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                // Buttons row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel
                    OutlinedButton(
                        onClick = {
                            newHabitName = ""
                            scope.launch { sheetState.hide() }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    // Add
                    Button(
                        onClick = {
                            if (newHabitName.isNotBlank()) {
                                onAddHabit(newHabitName.trim())
                                newHabitName = ""
                            }
                            scope.launch {

                                sheetState.hide()
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
        habits = habits,
        suggestions = suggestions,
        onToggleComplete = onToggleComplete,
        onAddHabit = { scope.launch { sheetState.show() } },
    )
}