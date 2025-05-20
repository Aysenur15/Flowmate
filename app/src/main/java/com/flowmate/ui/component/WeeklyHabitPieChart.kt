package com.flowmate.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeeklyHabitPieChart(habitCompletionMap: Map<String, Int>) {
    val total = habitCompletionMap.values.sum().toFloat()
    val colors = listOf(Color(0xFF4DB6AC), Color(0xFFB39DDB), Color(0xFFFFB74D))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(160.dp) // Rounded, consistent size
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = 0f
                val radius = size.minDimension / 2f
                val padding = 16.dp.toPx()
                val rect = Rect(
                    offset = Offset(padding, padding),
                    size = size.copy(
                        width = size.width - 2 * padding,
                        height = size.height - 2 * padding
                    )
                )

                habitCompletionMap.entries.forEachIndexed { index, entry ->
                    val sweepAngle = (entry.value / total) * 360f
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = rect.topLeft,
                        size = rect.size
                    )

                    val angle = startAngle + sweepAngle / 2
                    val labelX =
                        center.x + radius / 2 * kotlin.math.cos(Math.toRadians(angle.toDouble()))
                            .toFloat()
                    val labelY =
                        center.y + radius / 2 * kotlin.math.sin(Math.toRadians(angle.toDouble()))
                            .toFloat()
                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            "${(entry.value / total * 100).toInt()}%",
                            labelX,
                            labelY,
                            android.graphics.Paint().apply {
                                textSize = 32f
                                color = android.graphics.Color.BLACK
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                            }
                        )
                    }
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            habitCompletionMap.entries.forEachIndexed { index, entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color = colors[index % colors.size])
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.key,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

