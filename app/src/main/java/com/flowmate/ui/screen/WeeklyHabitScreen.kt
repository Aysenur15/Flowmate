package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flowmate.viewmodel.WeeklyHabitViewModel
import com.flowmate.ui.component.HabitStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WeeklyHabitScreen(viewModel: WeeklyHabitViewModel) {
    val habits = viewModel.weeklyHabits.collectAsState().value
    val daysOfWeek = DayOfWeek.values().toList() // Sunday to Saturday

    // 🔁 Dynamic current week range
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val endOfWeek = today.with(DayOfWeek.SUNDAY)
    val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    val weekRange = "${formatter.format(startOfWeek)} – ${formatter.format(endOfWeek)}"

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "This Week",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = weekRange,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                    text = day.name.take(3), // Mon, Tue...
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(habits) { habit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
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
                        val status = habit.weekStatus[day] ?: HabitStatus.NONE
                        val icon = when (status) {
                            HabitStatus.DONE -> Icons.Default.Check
                            HabitStatus.SKIPPED -> Icons.Default.SentimentSatisfied
                            HabitStatus.MISSED -> Icons.Default.Close
                            HabitStatus.NONE -> Icons.Default.RadioButtonUnchecked
                        }
                        val tint = when (status) {
                            HabitStatus.DONE -> Color(0xFF4CAF50)
                            HabitStatus.SKIPPED -> Color(0xFFFFC107)
                            HabitStatus.MISSED -> Color.Red
                            HabitStatus.NONE -> Color.Gray
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = status.name,
                            tint = tint,
                            modifier = Modifier
                                .weight(1f)
                                .size(24.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
