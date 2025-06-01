package com.flowmate.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// It contains the main frame of the app, with a navigation drawer, a top app bar and a bottom bar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFrame(
    onNavigateTo: (MainRoute) -> Unit,
    onLogout: () -> Unit,
    currentRoute: MainRoute,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxSize(0.7f),
                drawerTonalElevation = 16.dp,
                drawerShape = MaterialTheme.shapes.large,
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Flow Mate",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                DrawerItem("Calendar") { onNavigateTo(MainRoute.Calendar) }
                DrawerItem("Chronometer") { onNavigateTo(MainRoute.Chronometer) }
                DrawerItem("Reports") { onNavigateTo(MainRoute.Reports) }
                DrawerItem("Profile") { onNavigateTo(MainRoute.Profile) }
                DrawerItem("Theme") { onNavigateTo(MainRoute.Theme) }
                DrawerItem("Achievements") { onNavigateTo(MainRoute.Achievements) }
                DrawerItem("Settings") { onNavigateTo(MainRoute.Settings) }
                DrawerItem("Log Out") {
                    scope.launch {
                        drawerState.close()
                        onLogout()
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentRoute.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF64B2A9),
                        scrolledContainerColor = Color(0xFF64B2A9),
                    ),
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF6E6D6D),// MaterialTheme.colorScheme.secondaryContainer
                    contentColor = Color(0xFF7743CC),
                ) {
                    IconButton(
                        onClick = { onNavigateTo(MainRoute.Home) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onNavigateTo(MainRoute.Habits) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Habits",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onNavigateTo(MainRoute.Tasks) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddTask,
                            contentDescription = "Tasks",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        ) { paddingValues ->
            // Main content
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}

// 3) A little helper for drawer items
@Composable
private fun DrawerItem(text: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(text, style = MaterialTheme.typography.bodyLarge) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}