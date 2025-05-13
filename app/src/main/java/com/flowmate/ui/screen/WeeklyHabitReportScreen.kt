package com.flowmate.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowmate.ui.component.WeeklyHabitPieChart
import com.flowmate.viewmodel.ReportsViewModel

@Composable
fun WeeklyHabitReportScreen(viewModel: ReportsViewModel = viewModel()) {
    val data by viewModel.weeklyHabitData.collectAsState()

    if (data.isNotEmpty()) {
        WeeklyHabitPieChart(habitCompletionMap = data)
    } else {
        Text("No data available this week.")
    }
}