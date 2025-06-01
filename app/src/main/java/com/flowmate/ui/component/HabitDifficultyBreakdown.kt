package com.flowmate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.flowmate.ui.component.DifficultyCounts
import com.flowmate.viewmodel.ReportsViewModel

@Composable
fun HabitDifficultyBreakdown(userId: String) {
    val reportsViewModel: ReportsViewModel = viewModel()
    val rawData by reportsViewModel.difficultyRawData.collectAsState()

    LaunchedEffect(userId) {
        reportsViewModel.fetchDifficultyDataFromFirestore(userId)
    }

    if (rawData.isNotEmpty()) {
        val data = mapToDifficultyCounts(rawData)
        HabitDifficultyBreakdownGraph(data)
    } else {
        Text("Data loading...")
    }
}
// Composable function to display the breakdown graph
fun mapToDifficultyCounts(raw: List<List<Int>>): List<DifficultyCounts> {
    return raw.mapIndexed { index, dailyList ->
        var easy = 0
        var medium = 0
        var hard = 0

        dailyList.forEach { level ->
            when (level) {
                1, 2 -> easy++
                3 -> medium++
                4, 5 -> hard++
            }
        }

        DifficultyCounts(easy, medium, hard)
    }
}

