package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.HabitStatus
import com.flowmate.viewmodel.MonthlyHabitViewModel
import com.flowmate.viewmodel.WeeklyHabitViewModel
import com.flowmate.viewmodel.YearlyHabitViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun HabitProgressScreen(weeklyViewModel: WeeklyHabitViewModel,
                        monthlyViewModel: MonthlyHabitViewModel,
                        yearlyViewModel: YearlyHabitViewModel
) {
    var selectedView by remember { mutableStateOf("weekly") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // View Switcher Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedView = "weekly" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedView == "weekly") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Weekly")
            }

            Button(
                onClick = { selectedView = "monthly" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedView == "monthly") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Monthly")
            }

            Button(
                onClick = { selectedView = "yearly" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedView == "yearly") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Yearly")
            }
        }

        // Screen Content Based on Selection
        when (selectedView) {
            "weekly" -> WeeklyHabitScreen(viewModel = weeklyViewModel)
            "monthly" -> MonthlyHabitScreen(viewModel = monthlyViewModel)
            "yearly" -> YearlyHabitScreen(viewModel = yearlyViewModel)
        }
    }
}

@Composable
fun MonthlyHabitScreen(viewModel: MonthlyHabitViewModel) {
    val habits = viewModel.monthlyHabits.collectAsState().value
    val dateFormatter = DateTimeFormatter.ofPattern("d")

    // Renk paleti
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

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Şu anki ayı başlık olarak göster
        val currentMonth = YearMonth.now().month
        Text(
            text = "$currentMonth Habits",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        habits.forEachIndexed { index, habit ->
            val color = habitColors[index % habitColors.size]
            val totalDays = habit.monthStatus.size
            val completedDays = habit.monthStatus.count { it.value == HabitStatus.DONE }
            val progress = if (totalDays > 0) completedDays / totalDays.toFloat() else 0f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(color.copy(alpha = 0.25f), shape = MaterialTheme.shapes.medium)
                    .padding(12.dp)
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = color.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .background(
                                color = color,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
                Text(
                    text = "%${(progress * 100).toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp, top = 2.dp)
                )
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    habit.monthStatus.forEach { (date, status) ->
                        Column(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    val next = when (status) {
                                        HabitStatus.NONE -> HabitStatus.DONE
                                        HabitStatus.DONE -> HabitStatus.SKIPPED
                                        HabitStatus.SKIPPED -> HabitStatus.NONE
                                    }
                                    viewModel.updateHabitStatus(habit.id, date, next)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = date.format(dateFormatter))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = when (status) {
                                            HabitStatus.DONE -> color
                                            HabitStatus.SKIPPED -> MaterialTheme.colorScheme.error
                                            HabitStatus.NONE -> MaterialTheme.colorScheme.outline
                                        },
                                        shape = MaterialTheme.shapes.small
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YearlyHabitScreen(viewModel: YearlyHabitViewModel) {
    val year by viewModel.year.collectAsState()
    val completedDays by viewModel.completedDays.collectAsState()
    val months = (1..12).map { YearMonth.of(year, it) }
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val daysInLongestMonth = 31
    val heatmapColors = listOf(
        Color(0xFFFFBDAD), // light blue
        Color(0xFF90CAF9), // blue
        Color(0xFF1976D2), // dark blue
        Color(0xFFA5D6A7), // green
        Color(0xFFFFF59D), // yellow
        Color(0xFFFFAB91), // orange
        Color(0xFFF48FB1), // pink
        Color(0xFFB39DDB), // purple
        Color(0xFFB0BEC5)  // grey
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Yıl başlığı ve ileri/geri butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { viewModel.previousYear() },
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = ">",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { viewModel.nextYear() },
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Her ay için başarı oranı progress bar ve heatmap grid
        months.forEachIndexed { monthIndex, month ->
            val days = (1..month.lengthOfMonth()).map { day ->
                LocalDate.of(year, month.monthValue, day)
            }
            val completedInMonth = days.count { completedDays.contains(it) }
            val progress = if (days.isNotEmpty()) completedInMonth / days.size.toFloat() else 0f
            // Ay başlığı ve progress bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = monthNames[monthIndex],
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(40.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .background(
                            color = heatmapColors[monthIndex % heatmapColors.size].copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .background(
                                color = heatmapColors[monthIndex % heatmapColors.size],
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
                Text(
                    text = "%${(progress * 100).toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            // Heatmap grid: 31 günlük kutular
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                (1..daysInLongestMonth).forEach { dayNum ->
                    val date = if (dayNum <= days.size) days[dayNum - 1] else null
                    val isCompleted = date != null && completedDays.contains(date)
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                color = when {
                                    date == null -> Color.Transparent
                                    isCompleted -> heatmapColors[monthIndex % heatmapColors.size]
                                    else -> heatmapColors[monthIndex % heatmapColors.size].copy(alpha = 0.15f)
                                },
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
            }
        }
    }
}
