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
        Text(
            text = "Habit Progress",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        habits.forEach { habit ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
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
                                            HabitStatus.DONE -> MaterialTheme.colorScheme.primary
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            months.forEach { month ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(month.month.name.take(3)) // "JAN", "FEB", ...
                    Spacer(modifier = Modifier.height(4.dp))

                    val days = (1..month.lengthOfMonth()).map { day ->
                        LocalDate.of(year, month.monthValue, day)
                    }

                    val weeks = days.chunked(7)
                    weeks.forEach { week ->
                        Column {
                            week.forEach { date ->
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(1.dp)
                                        .background(
                                            color = if (completedDays.contains(date)) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
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
}

