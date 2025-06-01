package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.HabitStatus
import com.flowmate.viewmodel.WeeklyHabitViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// WeeklyHabitScreen displays a list of habits for the current week, allowing users to track their progress.
@Composable
fun WeeklyHabitScreen(viewModel: WeeklyHabitViewModel) {
    val habits = viewModel.weeklyHabits.collectAsState().value
    val today = LocalDate.now()
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedWeekStart by remember { mutableStateOf(today.with(DayOfWeek.MONDAY)) }
    val selectedWeekEnd = selectedWeekStart.plusDays(6)
    val daysOfWeek = DayOfWeek.values().toList() // Sunday to Saturday
    val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    val weekRange = "${formatter.format(selectedWeekStart)} – ${formatter.format(selectedWeekEnd)}"
    val habitColors = listOf(
        Color(0xFFB39DDB), Color(0xFF80CBC4), Color(0xFFFFAB91), Color(0xFFA5D6A7), Color(0xFFFFF59D),
        Color(0xFF90CAF9), Color(0xFFE6EE9C), Color(0xFFFFCC80), Color(0xFFF48FB1), Color(0xFFB0BEC5)
    )

    // Update habits when the selected week changes
    androidx.compose.runtime.LaunchedEffect(selectedWeekStart) {
        viewModel.fetchHabitsForWeek(context, selectedWeekStart, selectedWeekEnd)
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { selectedWeekStart = selectedWeekStart.minusWeeks(1) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Previous Week",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = weekRange,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (selectedWeekStart == today.with(DayOfWeek.MONDAY)) "This week" else "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(
                onClick = { selectedWeekStart = selectedWeekStart.plusWeeks(1) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next week",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        // Header row for the habit grid
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Habit",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.width(80.dp)
            )
            daysOfWeek.forEach { day ->
                Text(
                    text = day.name.take(3),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(habits) { habitIndex, habit ->
                val color = habitColors[habitIndex % habitColors.size]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color.copy(alpha = 0.25f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = habit.title,
                        modifier = Modifier.width(80.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    daysOfWeek.forEach { day ->
                        val date = selectedWeekStart.with(day)
                        val status = habit.weekStatus[day] ?: HabitStatus.NONE
                        val icon = when (status) {
                            HabitStatus.DONE -> Icons.Default.Check
                            HabitStatus.SKIPPED -> Icons.Default.SentimentSatisfied
                            HabitStatus.NONE -> Icons.Default.RadioButtonUnchecked
                        }
                        val tint = when (status) {
                            HabitStatus.DONE -> color
                            HabitStatus.SKIPPED -> Color(0xFFFFC107)
                            HabitStatus.NONE -> MaterialTheme.colorScheme.onSurface
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier
                                .weight(1f)
                                .size(28.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

