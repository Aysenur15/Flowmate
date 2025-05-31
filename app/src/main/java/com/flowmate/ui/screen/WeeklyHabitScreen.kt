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
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

    val habitColors = listOf(
        androidx.compose.ui.graphics.Color(0xFFB39DDB), // mor
        androidx.compose.ui.graphics.Color(0xFF80CBC4), // turkuaz
        androidx.compose.ui.graphics.Color(0xFFFFAB91), // turuncu
        androidx.compose.ui.graphics.Color(0xFFA5D6A7), // yeşil
        androidx.compose.ui.graphics.Color(0xFFFFF59D), // sarı
        androidx.compose.ui.graphics.Color(0xFF90CAF9), // mavi
        androidx.compose.ui.graphics.Color(0xFFE6EE9C), // açık yeşil
        androidx.compose.ui.graphics.Color(0xFFFFCC80), // açık turuncu
        androidx.compose.ui.graphics.Color(0xFFF48FB1), // pembe
        androidx.compose.ui.graphics.Color(0xFFB0BEC5)  // gri
    )

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
                        val status = habit.weekStatus[day] ?: HabitStatus.NONE
                        val icon = when (status) {
                            HabitStatus.DONE -> Icons.Default.Check
                            HabitStatus.SKIPPED -> Icons.Default.SentimentSatisfied
                            HabitStatus.NONE -> Icons.Default.RadioButtonUnchecked
                        }
                        val tint = when (status) {
                            HabitStatus.DONE -> color
                            HabitStatus.SKIPPED -> Color(0xFFFFC107)
                            HabitStatus.NONE -> Color.Gray
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
