package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.HabitStatus
import com.flowmate.viewmodel.MonthlyHabitViewModel
import com.flowmate.viewmodel.YearlyHabitViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.flowmate.viewmodel.WeeklyHabitViewModel

@Composable
fun HabitProgressScreen(
    weeklyViewModel: WeeklyHabitViewModel,
    monthlyViewModel: MonthlyHabitViewModel,
    yearlyViewModel: YearlyHabitViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        weeklyViewModel.fetchHabitsFromFirestore(context)
        monthlyViewModel.fetchHabitsFromFirestore(context)
        yearlyViewModel.fetchHabitsFromFirestore(context)
    }

    var selectedView by remember { mutableStateOf("monthly") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
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

        when (selectedView) {
            "monthly" -> MonthlyHabitScreen(viewModel = monthlyViewModel)
            "yearly" -> YearlyHabitScreen(viewModel = yearlyViewModel)
        }
    }
}

@Composable
fun MonthlyHabitScreen(viewModel: MonthlyHabitViewModel) {
    val habits by viewModel.monthlyHabits.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("d")
    val habitColors = listOf(
        Color(0xFFB39DDB), Color(0xFF80CBC4), Color(0xFFFFAB91), Color(0xFFA5D6A7),
        Color(0xFFFFF59D), Color(0xFF90CAF9), Color(0xFFE6EE9C), Color(0xFFFFCC80),
        Color(0xFFF48FB1), Color(0xFFB0BEC5)
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
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
                Text(text = habit.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                LinearProgressIndicator(progress = progress, color = color, modifier = Modifier.fillMaxWidth().height(8.dp))
                Text(text = "%${(progress * 100).toInt()}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))

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

    Column(modifier = Modifier.fillMaxSize().padding(8.dp).verticalScroll(rememberScrollState())) {
        val context = LocalContext.current
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("<", modifier = Modifier.clickable { viewModel.previousYear(context) }, style = MaterialTheme.typography.titleLarge)
            Text(year.toString(), style = MaterialTheme.typography.headlineSmall)
            Text(">", modifier = Modifier.clickable { viewModel.nextYear(context) }, style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        months.forEachIndexed { monthIndex, month ->
            val days = (1..month.lengthOfMonth()).map { LocalDate.of(year, month.monthValue, it) }
            val completedInMonth = days.count { completedDays.contains(it) }
            val progress = completedInMonth / days.size.toFloat()

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text("${monthNames[monthIndex]} - %${(progress * 100).toInt()}", style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(8.dp))
            }
        }
    }
}
