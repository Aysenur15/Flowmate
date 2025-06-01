package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flowmate.repository.HabitRepository
import com.flowmate.ui.component.Habit
import com.flowmate.ui.component.MonthlyHabitViewModelFactory
import com.flowmate.ui.component.SmartSuggestion
import com.flowmate.ui.component.WeeklyHabitViewModelFactory
import com.flowmate.ui.component.YearlyHabitViewModelFactory
import com.flowmate.ui.theme.HabitProgressColor
import com.flowmate.ui.theme.TickColor
import com.flowmate.viewmodel.MonthlyHabitViewModel
import com.flowmate.viewmodel.MyHabitsViewModal
import com.flowmate.viewmodel.WeeklyHabitViewModel
import com.flowmate.viewmodel.YearlyHabitViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHabitsScreen(
    habits: List<Habit>,
    suggestions: List<SmartSuggestion>,
    onToggleComplete: (habitId: String) -> Unit,
    onAddHabit: () -> Unit,
    navController: NavController
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val monthlyHabitViewModel: MonthlyHabitViewModel = viewModel(
        factory = MonthlyHabitViewModelFactory(HabitRepository(), userId ?: "")
    )
    val yearlyHabitViewModel: YearlyHabitViewModel = viewModel(
        factory = YearlyHabitViewModelFactory(HabitRepository(), userId ?: "")
    )
    var habitList: List<Habit> by remember { mutableStateOf<List<Habit>>(emptyList()) }

    LaunchedEffect(key1 = Unit) {
        if (userId != null) {
            monthlyHabitViewModel.fetchHabitsFromFirestore(navController.context)
            yearlyHabitViewModel.fetchHabitsFromFirestore(navController.context)
        }
    }

    val habitRepository = remember { HabitRepository() }
    val habitViewModel = remember { MyHabitsViewModal() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val habitColorsLight = listOf(
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
    val habitColorsDark = listOf(
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

    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var editFrequency by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
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
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("habitProgress") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("View Progress")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits) { habit ->
                    val isDark = isSystemInDarkTheme()
                    val colorList = if (isDark) habitColorsDark else habitColorsLight
                    val cardBg = colorList[habit.id.hashCode().let { if (it < 0) -it else it } % colorList.size]
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                editingHabit = habit
                                editFrequency = habit.frequency ?: ""
                            }
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
                                modifier = Modifier.weight(1f),
                                color = if (isDark) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(16.dp))
                            IconButton(onClick = {
                                scope.launch {
                                    habitRepository.markHabitCompletedForToday(userId.toString(), habit.id)
                                    habitList = habitRepository.getHabitsFromFirestore(userId.toString())

                                }
                            }) {
                                val todayMillis = java.time.LocalDate.now()
                                    .atStartOfDay(java.time.ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()
                                val icon = if (habit.completedDates.contains(todayMillis))
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
        }

        if (editingHabit != null) {
            ModalBottomSheet(
                onDismissRequest = { editingHabit = null },
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text("Edit Habit", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Text("Title: ${editingHabit?.title}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editFrequency,
                        onValueChange = { editFrequency = it },
                        label = { Text("Recurrence (e.g. 3 per week)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            editingHabit?.let { habit ->
                                scope.launch {
                                    habitRepository.markHabitCompletedForToday(userId.toString(), habit.id)
                                    habitRepository.getHabitsFromFirestore(userId.toString())
                                    editingHabit = editingHabit?.copy(isCompletedToday = true)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as Completed")
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            editingHabit?.let { habit ->
                                scope.launch {
                                    habitRepository.updateHabitFrequency(userId.toString(), habit.id, editFrequency)
                                    editingHabit = editingHabit?.copy(frequency = editFrequency)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Recurrence")
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Habit", color = Color.White)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { editingHabit = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }

        if (showDeleteConfirm && editingHabit != null) {
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
                    Text("Are you sure you want to delete this habit?", color = Color.Red)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = {
                            editingHabit?.let { habit ->
                                scope.launch {
                                    habitRepository.deleteHabitCompletely(context, userId.toString(), habit)
                                    editingHabit = null
                                    showDeleteConfirm = false
                                }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
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
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val weeklyHabitViewModel: WeeklyHabitViewModel = viewModel(
        factory = WeeklyHabitViewModelFactory(HabitRepository(), userId ?: "")
    )
    val monthlyHabitViewModel: MonthlyHabitViewModel = viewModel(
        factory = MonthlyHabitViewModelFactory(HabitRepository(), userId ?: "")
    )
    val yearlyHabitViewModel: YearlyHabitViewModel = viewModel(
        factory = YearlyHabitViewModelFactory(HabitRepository(), userId ?: "")
    )

    LaunchedEffect(key1 = Unit) {
        if (userId != null) {
            weeklyHabitViewModel.fetchHabitsFromFirestore(navController.context)
            monthlyHabitViewModel.fetchHabitsFromFirestore(navController.context)
            yearlyHabitViewModel.fetchHabitsFromFirestore(navController.context)
        }
    }
    val habitRepository = remember { HabitRepository() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    var habitList by remember { mutableStateOf<List<Habit>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            habitList = habitRepository.getHabitsFromFirestore(userId)
        }
    }

    val onToggleCompleteHandler: (String) -> Unit = { habitId ->
        if (userId != null) {
            scope.launch {
                android.util.Log.d("MyHabitsScreen", " Toggling completion for habit: $habitId")

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
                                        habitList = habitRepository.getHabitsFromFirestore(userId) // <-- Ekleme sonrası listeyi güncelle
                                    } catch (e: Exception) {

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

