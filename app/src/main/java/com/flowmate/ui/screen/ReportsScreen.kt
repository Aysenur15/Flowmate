package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowmate.ui.component.WeeklyHabitPieChart
import com.flowmate.ui.component.HabitTimeBarChart
import com.flowmate.viewmodel.ReportsViewModel
import com.flowmate.viewmodel.TimerStatsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowmate.ui.component.HabitConsistencyGrid
import com.flowmate.ui.component.HabitDifficultyBreakdown
import androidx.compose.ui.Alignment


// 1. Model for a detailed report entry (could be a chart, table, etc.)
data class ReportEntry(
    val title: String,
    val description: String
)

// 2. Reports screen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = viewModel(),
    timerViewModel: TimerStatsViewModel = viewModel(),
    weeklyProgress: Float,
    monthlyProgress: Float,
    yearlyProgress: Float,
    entries: List<ReportEntry>,
    onRefresh: () -> Unit,
    onEntryClick: (ReportEntry) -> Unit
) {
    val habitData by viewModel.weeklyHabitData.collectAsState()
    val selectedRange by timerViewModel.selectedRange.collectAsState()
    val timeSegments by timerViewModel.timeSegments.collectAsState()

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            if (habitData.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        WeeklyHabitPieChart(habitCompletionMap = habitData)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        HabitDifficultyBreakdown(
                            difficultyData = mapOf(
                                "Reading" to listOf(
                                    "Mon" to Color(0xFFFFCC80), "Tue" to Color(0xFF81C784), "Wed" to Color(0xFF81C784),
                                    "Thu" to Color(0xFF81C784), "Fri" to Color(0xFF81C784), "Sat" to Color(0xFF81C784), "Sun" to Color(0xFF81C784)
                                ),
                                "Meditation" to listOf(
                                    "Mon" to Color(0xFFFFAB91), "Tue" to Color(0xFF4DB6AC), "Wed" to Color(0xFF4DB6AC),
                                    "Thu" to Color(0xFF4DB6AC), "Fri" to Color(0xFF4DB6AC), "Sat" to Color(0xFF4DB6AC), "Sun" to Color(0xFF4DB6AC)
                                ),
                                "New Habit" to List(7) { "" to Color(0xFFB39DDB) }
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 64.dp)
                        ) {
                            listOf(
                                "Easy" to Color(0xFFFFCC80),
                                "Medium" to Color(0xFFFFAB91),
                                "Hard" to Color(0xFF4DB6AC),
                                "Completed" to Color(0xFF81C784),
                                "Planned" to Color(0xFFB39DDB)
                            ).forEach { (label, color) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(color, shape = RoundedCornerShape(2.dp))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = label, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Add the Habit Time Bar Chart here
            HabitTimeBarChart(
                segments = timeSegments,
                selectedRange = selectedRange,
                onRangeSelected = { timerViewModel.onRangeSelected(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    HabitConsistencyGrid(
                        habitData = mapOf(
                            "Exercise" to listOf(true, false, true, true, false),
                            "Reading" to listOf(true, true, true, true, true),
                            "Meditation" to listOf(false, false, true, true, false)
                        )
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

            }

            Spacer(modifier = Modifier.height(24.dp))


                // 4 detailed entries (e.g. pie charts, line charts, tables…)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries) { entry ->
                        Card(
                            onClick = { onEntryClick(entry) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(entry.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(entry.description, style = MaterialTheme.typography.bodyMedium)
                                // Placeholder for future charts
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
