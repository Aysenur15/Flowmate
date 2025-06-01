package com.flowmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowmate.ui.component.AiSuggestion
import com.flowmate.ui.component.DailyMessageCard
import com.flowmate.ui.component.HabitConsistencyGrid
import com.flowmate.ui.component.HabitDifficultyBreakdown
import com.flowmate.ui.component.HabitTimeBarChart
import com.flowmate.ui.component.StyledAiInsights
import com.flowmate.ui.component.WeeklyHabitPieChart
import com.flowmate.ui.component.getTodayQuote
import com.flowmate.viewmodel.ReportsViewModel
import com.flowmate.viewmodel.TimerStatsViewModel
import com.google.firebase.auth.FirebaseAuth

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
    val habitConsistency by viewModel.habitConsistency.collectAsState()
    val selectedRange by timerViewModel.selectedRange.collectAsState()
    val timeSegments by timerViewModel.timeSegments.collectAsState()

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val reportDate = "2025-05-28"

    // Kullanıcının alışkanlıklarını Firestore'dan çekip AI önerisi al
    LaunchedEffect(userId) {
        if (userId != null) {
            val habits = mutableListOf<Pair<String, Int>>()
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("users")
                .document(userId)
                .collection("habits")
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val title = doc.getString("title") ?: return@forEach
                        val level = (doc.get("difficultyLevel") as? Long)?.toInt() ?: 1
                        habits.add(title to level)
                    }
                    viewModel.fetchAiSuggestions(userName = userId, habits = habits)
                }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchHabitDurationsForDate(userId = userId.toString(), date = reportDate)
        viewModel.fetchHabitTimeSegments(userId = userId.toString(), date = reportDate)
        viewModel.fetchHabitConsistencyFromCompletedDates(userId = userId.toString())
        viewModel.fetchDifficultyDataFromFirestore(userId.toString())
    }

    Scaffold { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ✅ Weekly pie chart (7-day summary)
            if (habitData.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        WeeklyHabitPieChart(habitCompletionMap = habitData)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // ✅ Time bar chart (daily/weekly/monthly)
            HabitTimeBarChart(
                segments = timeSegments,
                selectedRange = selectedRange,
                onRangeSelected = { timerViewModel.onRangeSelected(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Habit consistency grid and difficulty breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HabitConsistencyGrid(habitData = habitConsistency)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Difficulty Breakdown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // ✅ Dynamic breakdown using Firestore data
                    HabitDifficultyBreakdown(userId = userId.toString())

                    Spacer(modifier = Modifier.height(24.dp))

                    DailyMessageCard(
                        quote = getTodayQuote().first,
                        author = getTodayQuote().second
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI'dan gelen önerileri 5 kart olarak göster
                    val aiSuggestionsRaw by viewModel.aiSuggestions.collectAsState()
                    val aiSuggestionsList = aiSuggestionsRaw
                        .replace("**", "") // Gemini'dan gelen sonuçta ** işaretlerini sil
                        .split("\n")
                        .filter { it.isNotBlank() }
                        .take(5)
                        .mapIndexed { idx, suggestion ->
                            val parts = suggestion.split(":", limit = 2)
                            val title = parts.getOrNull(0)?.trim() ?: "Suggestion ${idx+1}"
                            val message = parts.getOrNull(1)?.trim() ?: suggestion.trim()
                            val icons = listOf("💡", "🔥", "🌱", "🚀", "⭐")
                            com.flowmate.ui.component.AiSuggestion(
                                icon = icons.getOrElse(idx) { "💡" },
                                title = title,
                                message = message
                            )
                        }
                    StyledAiInsights(insights = aiSuggestionsList)
                }
            }

            // ✅ Optional report cards at bottom
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
