package com.flowmate.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HabitDifficultyBreakdown(difficultyData: Map<String, List<Pair<String, Color>>>) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .width(180.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Difficulty Breakdown",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Horizontal chart per day
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val barWidth = 24.dp
        val barSpacing = 8.dp
        val habitNames = difficultyData.keys.toList() + "New Habit"

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            days.forEachIndexed { dayIndex, day ->
                Column(
                    modifier = Modifier
                        .padding(end = barSpacing)
                        .height(80.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Canvas(modifier = Modifier
                        .width(barWidth)
                        .height(60.dp)) {
                        var currentTop = size.height
                        for (habit in habitNames.reversed()) {
                            val color = difficultyData[habit]?.getOrNull(dayIndex)?.second ?: Color.LightGray
                            val blockHeight = size.height / habitNames.size
                            currentTop -= blockHeight
                            drawRect(
                                color = color,
                                topLeft = androidx.compose.ui.geometry.Offset(0f, currentTop),
                                size = androidx.compose.ui.geometry.Size(size.width, blockHeight)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(day, fontSize = 12.sp)
                }
            }
        }
    }
}
