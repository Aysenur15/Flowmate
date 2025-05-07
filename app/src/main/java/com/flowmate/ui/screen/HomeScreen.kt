package com.flowmate.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flowmate.ui.component.MainRoute
import com.flowmate.ui.theme.ButtonShape

@Composable
fun HomeScreen(
    modifier: Modifier,
    userName: String,
    onNavigateTo: (MainRoute) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Welcome $userName",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Motivation",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))

        // Your five motivation buttons
        val items = listOf(
            "My Habits" to MainRoute.Habits,
            "My Tasks" to MainRoute.Tasks,
            "Calendar" to MainRoute.Calendar,
            "Kronometer" to MainRoute.Chronometer,
            "Reports" to MainRoute.Reports
        )
        items.forEach { (label, screen) ->
            Button(
                onClick = { onNavigateTo(screen) },
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}