package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowmate.ui.component.*
import com.flowmate.viewmodel.ReportsViewModel
import com.flowmate.viewmodel.TimerStatsViewModel

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
        val scrollState = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

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
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    HabitConsistencyGrid(
                        habitData = mapOf(
                            "Exercise" to listOf(true, false, true, true, false),
                            "Reading" to listOf(true, true, true, true, true),
                            "Meditation" to listOf(false, false, true, true, false)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val (quote, author) = getTodayQuote()
                    DailyMessageCard(quote = quote, author = author)
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf(
                                "Easy" to Color(0xFFFFCC80),
                                "Medium" to Color(0xFFFFAB91),
                                "Hard" to Color(0xFF4DB6AC)
                            ).forEach { (label, color) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(color, shape = RoundedCornerShape(2.dp))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = label, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    StyledAiInsights(
                        insights = listOf(
                            AiSuggestion("🌞", "Best Time", "You are most productive in the morning."),
                            AiSuggestion("🧠", "Focus Strategy", "Try scheduling harder habits in the afternoon."),
                            AiSuggestion("🎉", "Streak Tracker", "You've built a 10-day streak!")
                        )
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                entries.forEach { entry ->
                    Card(
                        onClick = { onEntryClick(entry) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(entry.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(entry.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}
