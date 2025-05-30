package com.flowmate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowmate.ui.component.DifficultyCounts

@Composable
fun HabitDifficultyBreakdownGraph(data: List<DifficultyCounts>) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val EasyColor = Color(0xFFE7C293)
    val MediumColor = Color(0xFFFFAB91)
    val HardColor = Color(0xFF4DB6AC)
    val maxBarHeight = 100.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxBarHeight),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            dayLabels.forEachIndexed { index, day ->
                val dayData = data.getOrNull(index) ?: DifficultyCounts(0, 0, 0)
                val total = (dayData.easy + dayData.medium + dayData.hard).coerceAtLeast(1)
                val unitHeight = maxBarHeight.value / total

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .height((dayData.hard * unitHeight).dp)
                            .width(20.dp)
                            .background(HardColor)
                    )
                    Box(
                        modifier = Modifier
                            .height((dayData.medium * unitHeight).dp)
                            .width(20.dp)
                            .background(MediumColor)
                    )
                    Box(
                        modifier = Modifier
                            .height((dayData.easy * unitHeight).dp)
                            .width(20.dp)
                            .background(EasyColor)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = day, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            listOf(
                "Easy" to EasyColor,
                "Medium" to MediumColor,
                "Hard" to HardColor
            ).forEach { (label, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(color, RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = label, fontSize = 14.sp)
                }
            }
        }
    }
}
