package com.flowmate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HabitConsistencyGrid(habitData: Map<String, List<Boolean>>) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .width(160.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Habit Consistency",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        habitData.forEach { (habit, days) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = habit,
                    fontSize = 12.sp,
                    modifier = Modifier.width(48.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    days.forEach { isCompleted ->
                        Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (isCompleted) Color(0xFFFF5722) else Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    }
                }
            }
        }
    }
}
