package com.flowmate.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun rememberCurrentRoute(navController: NavHostController): String {
    var currentRoute by remember { mutableStateOf(MainRoute.Home.route) }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute = destination.route ?: MainRoute.Home.route
        }
    }

    return currentRoute
}