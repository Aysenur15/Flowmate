package com.flowmate.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flowmate.R
import com.flowmate.ui.component.MainRoute
import com.flowmate.ui.component.getTodayQuote
import com.flowmate.ui.theme.ButtonShape


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (MainRoute) -> Unit,
    userName: String,

) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // Greeting Section
        Text(
            text = "Welcome $userName",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        val (quote, author) = getTodayQuote()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer // or any color you like
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "\"$quote\"",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer // ensure readable contrast
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Motivational Image
        Image(
            painter = painterResource(id = R.drawable.todo),
            contentDescription = "Motivational Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Explore Label
        Text(
            text = "Explore Your Journey",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        // Navigation Buttons
        val items = listOf(
            "My Habits" to MainRoute.Habits,
            "My Tasks" to MainRoute.Tasks,
            "Calendar" to MainRoute.Calendar,
            "Chronometer" to MainRoute.Chronometer,
            "Reports" to MainRoute.Reports
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { (label, screen) ->
                Button(
                    onClick = { onNavigateTo(screen) },
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
