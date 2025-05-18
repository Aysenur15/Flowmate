package com.flowmate.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowmate.viewmodel.HabitTimeSegment
import com.flowmate.viewmodel.TimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTimeBarChart(
    segments: List<HabitTimeSegment>,
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedRange.name) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.End
    ){
            // Framed compact dropdown
            Box(modifier = Modifier.width(120.dp)) {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(fontSize = 0.sp),
                    singleLine = true,
                    label = { Text("Time") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Time Range",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.height(44.dp)
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(120.dp)
                ) {
                    TimeRange.values().forEach { range ->
                        val isSelected = range.name == selectedText
                        DropdownMenuItem(
                            modifier = Modifier.background(
                                if (isSelected)
                                    Color(0xFFFF9800).copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            text = { Text(range.name) },
                            onClick = {
                                expanded = false
                                selectedText = range.name
                                onRangeSelected(range)
                            }
                        )
                    }
                }
            }

            val totalMinutes = segments.sumOf { it.minutes }
            val totalHours = totalMinutes / 60
            val remainingMinutes = totalMinutes % 60

            Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${totalHours}h ${remainingMinutes}m",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .padding(bottom = 6.dp)
                .align(Alignment.Start)
        )


            val barHeight: Dp = 24.dp

            if (totalMinutes > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .background(Color.LightGray, shape = RoundedCornerShape(6.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startX = 0f
                        segments.forEach { segment ->
                            val ratio = segment.minutes.toFloat() / totalMinutes
                            val width = size.width * ratio
                            drawRect(
                                color = segment.color,
                                topLeft = Offset(startX, 0f),
                                size = androidx.compose.ui.geometry.Size(width, size.height)
                            )
                            startX += width
                        }
                    }
                }
            } else {
                Text("No data available for this range.", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend (Horizontal Row)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                segments.forEach { segment ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(segment.color, shape = RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = segment.habitName, fontSize = 13.sp)
                    }
                }
            }
        }
}
