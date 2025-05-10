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
import com.flowmate.ui.screen.MyHabitsWithModalSheet
import com.flowmate.ui.screen.MyTasksScreen
import com.flowmate.ui.screen.ProfileScreen
import com.flowmate.ui.screen.ReportsScreen
import com.flowmate.ui.screen.SignUpScreen
import com.flowmate.viewmodel.AuthViewModel
import com.flowmate.viewmodel.MyHabitsViewModal
import com.flowmate.viewmodel.MyTasksViewModal

@Composable
fun FlowMateNavGraph(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentRoute = rememberCurrentRoute(navController)
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsState(initial = false)

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("auth") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    if (isLoggedIn) {
        MainFrame(
            onNavigateTo = { route -> navController.navigate(route.route) },
            onLogout = { authViewModel.signOut() },
            currentDestination = currentRoute,
        ) {
            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                mainNavGraph(navController)
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
    navController: NavHostController
) {
    // Use the existing authViewModel instance to get tasks
    val authViewModel = AuthViewModel()
    val myTasksViewModal = MyTasksViewModal()
    val myHabitViewModal = MyHabitsViewModal()

    navigation(startDestination = MainRoute.Home.route, route = "main") {

        composable(MainRoute.Home.route) {
            val userName by authViewModel.currentUserName.collectAsState()
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
            val habits by myHabitViewModal.habits.collectAsState(initial = emptyList())
            val suggestions by myHabitViewModal.habitSuggestions.collectAsState(initial = emptyList())

            MyHabitsWithModalSheet(
                habits = habits,
                suggestions = suggestions,
                onToggleComplete = { habit ->
                    myHabitViewModal.toggleHabitCompletion(habit)
                },
                onAddHabit = {
                },
            )
        }


        composable(MainRoute.Tasks.route) {
            val tasks by myTasksViewModal.tasks.collectAsState(initial = emptyList())
            val suggestions by myTasksViewModal.tasks.collectAsState(initial = emptyList())

            MyTasksScreen(
                tasks = tasks,
                onToggleTask = { task ->
                    myTasksViewModal.toggleTaskCompletion(task)
                },
                onAddTask = {
                    // Handle adding a new task

                    //navController.navigate(MainRoute.AddTask.route)
                },
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
                onEntryClick = { entry -> /* Handle entry click */ },
                onRefresh = { /* Handle refresh */ },
                weeklyProgress = 0f, // Replace with actual progress
                yearlyProgress = 0f, // Replace with actual progress
                monthlyProgress = 0f // Replace with actual progress
            )
        }
        composable(MainRoute.Profile.route) {
            ProfileScreen(
                currentName = authViewModel.currentUserName.collectAsState().value,
                onNameChange = { /* Handle name change */ },
                onSaveName = { /* Handle save name */ },
                onResetProgress = { /* Handle reset progress */ },
                onExportData = { /* Handle export data */ }
            )
        }
        composable(MainRoute.Theme.route) {
            // TODO: Theme settings screen logic
        }
        composable(MainRoute.Achievements.route) {
            // TODO: Achievements screen logic
        }
        composable(MainRoute.Settings.route) {
            // TODO: Settings screen logic
        }
    }
}
