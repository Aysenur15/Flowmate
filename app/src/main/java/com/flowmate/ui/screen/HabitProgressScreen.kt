package com.flowmate.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flowmate.viewmodel.WeeklyHabitViewModel

@Composable
fun HabitProgressScreen(viewModel: WeeklyHabitViewModel) {
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
            "weekly" -> WeeklyHabitScreen(viewModel = viewModel)
            "monthly" -> MonthlyHabitScreen()
            "yearly" -> YearlyHabitScreen()
        }
    }
}

@Composable
fun MonthlyHabitScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Monthly Habit View (Coming Soon)")
    }
}

@Composable
fun YearlyHabitScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Yearly Habit View (Coming Soon)")
    }
}
