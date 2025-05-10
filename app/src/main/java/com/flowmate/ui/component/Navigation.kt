package com.flowmate.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.flowmate.ui.screen.CalendarScreen
import com.flowmate.ui.screen.ChronometerScreen
import com.flowmate.ui.screen.HomeScreen
import com.flowmate.ui.screen.LoginScreen
import com.flowmate.ui.screen.MainFrame
import com.flowmate.ui.screen.MyHabitScreen
import com.flowmate.ui.screen.MyTasksScreen
import com.flowmate.ui.screen.ReportsScreen
import com.flowmate.ui.screen.SignUpScreen
import com.flowmate.viewmodel.AuthViewModel

// 1. Define your routes
sealed class AuthRoute(val route: String) {
    data object Login : AuthRoute("login")
    data object SignUp : AuthRoute("signup")
}

sealed class MainRoute(val route: String) {
    data object Home : MainRoute("home")
    data object Habits : MainRoute("habits")
    data object Tasks : MainRoute("tasks")
    data object Calendar : MainRoute("calendar")
    data object Chronometer : MainRoute("chronometer")
    data object Reports : MainRoute("reports")
    data object Profile : MainRoute("profile")
    data object Theme : MainRoute("theme")
    data object Achievements : MainRoute("achievements")
    data object Settings : MainRoute("settings")
}

@Composable
fun FlowMateNavGraph(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsState(initial = false)
    val userName by authViewModel.currentUserName.collectAsState(initial = "")

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("auth") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    if (isLoggedIn) {
        MainFrame(
            userName = userName,
            onNavigateTo = { route -> navController.navigate(route.route) },
            onLogout = { authViewModel.signOut() },
        ) {
            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                mainNavGraph(navController, authViewModel)
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "auth"
        ) {
            authNavGraph(navController, authViewModel)
        }
    }
}

private fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = AuthRoute.Login.route, route = "auth") {
        composable(AuthRoute.Login.route) {
            LoginScreen(
                onLogin = { u, p -> authViewModel.signIn(u.trim(), p) },
                onNavigateToSignUp = { navController.navigate(AuthRoute.SignUp.route) }
            )
        }
        composable(AuthRoute.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate(AuthRoute.Login.route) },
                onSignUp = { name, email, user, pass ->
                    authViewModel.signUp(name, email, user, pass)
                }
            )
        }
    }
}

private fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = MainRoute.Home.route, route = "main") {
        composable(MainRoute.Home.route) {
            // 2️⃣ collect your flows here
            val userName by authViewModel.currentUserName.collectAsState(initial = "")
            HomeScreen(
                modifier = Modifier,
                userName = userName,
                onNavigateTo = { route ->
                    navController.navigate(route.route) {
                        popUpTo(MainRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(MainRoute.Habits.route) {
            val habits by authViewModel.habits.collectAsState(initial = emptyList())
            val suggestions by authViewModel.habitSuggestions.collectAsState(initial = emptyList())
            MyHabitScreen(
                habits = habits,
                suggestions = suggestions,
                onToggleComplete = authViewModel::toggleHabitCompletion,
                onAddHabit = { /*…*/ }
            )
        }
        composable(MainRoute.Tasks.route) {
            MyTasksScreen(
                tasks = emptyList(), // Replace with actual task list
                onAddTask = { /* Handle adding a new task */ },
                onToggleTask = { taskId ->
                    // Handle toggling task completion
                    /**/
                }
            )
        }
        composable(MainRoute.Calendar.route) {
            CalendarScreen()
        }
        composable(MainRoute.Chronometer.route) {
            ChronometerScreen()
        }
        composable(MainRoute.Reports.route) {
            ReportsScreen(
                entries = emptyList(), // Replace with actual entries
                onEntryClick = { entry ->
                    // Handle entry click
                    /**/
                },
                onRefresh = { /* Handle refresh */ },
                weeklyProgress = 0f, // Replace with actual progress
                yearlyProgress = 0f, // Replace with actual progress
                monthlyProgress = 0f // Replace with actual progress
            )
        }
        composable(MainRoute.Profile.route) {
            //TODO
        }
        composable(MainRoute.Theme.route) {
            //TODO
        }
        composable(MainRoute.Achievements.route) {
            //TODO
        }
        composable(MainRoute.Settings.route) {
            //TODO
        }
        // Add more composable destinations as needed
    }
}
