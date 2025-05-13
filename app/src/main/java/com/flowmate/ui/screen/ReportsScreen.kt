package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.WeeklyHabitPieChart
import com.flowmate.viewmodel.ReportsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


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
    weeklyProgress: Float,
    monthlyProgress: Float,
    yearlyProgress: Float,
    entries: List<ReportEntry>,
    onRefresh: () -> Unit,
    onEntryClick: (ReportEntry) -> Unit
) {
    val habitData by viewModel.weeklyHabitData.collectAsState()

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Chart shown at top
            if (habitData.isNotEmpty()) {
                WeeklyHabitPieChart(habitCompletionMap = habitData)
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Text("No weekly habit data yet.")
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3 progress circles row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProgressCircle(label = "Weekly", progress = weeklyProgress)
                ProgressCircle(label = "Monthly", progress = monthlyProgress)
                ProgressCircle(label = "Yearly", progress = yearlyProgress)
            }

            Spacer(Modifier.height(32.dp))

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


// 3. A little helper for the circular stats
@Composable
private fun ProgressCircle(label: String, progress: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .background(Color.Transparent, shape = CircleShape)
        ) {
            CircularProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                strokeWidth = 8.dp,
                modifier = Modifier.fillMaxSize()
            )
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
