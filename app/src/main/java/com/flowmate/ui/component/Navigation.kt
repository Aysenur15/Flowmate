package com.flowmate.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.flowmate.FlowMateApp.Companion.database
import com.flowmate.data.UserEntity
import com.flowmate.repository.AuthRepository
import com.flowmate.ui.screen.CalendarScreen
import com.flowmate.ui.screen.ChronometerScreen
import com.flowmate.ui.screen.EditCredentialsScreen
import com.flowmate.ui.screen.HabitProgressScreen
import com.flowmate.ui.screen.HomeScreen
import com.flowmate.ui.screen.LoginScreen
import com.flowmate.ui.screen.MyHabitsWithModalSheet
import com.flowmate.ui.screen.MyTasksScreen
import com.flowmate.ui.screen.ProfileScreen
import com.flowmate.ui.screen.ReportsScreen
import com.flowmate.ui.screen.SettingsScreen
import com.flowmate.ui.screen.SignUpScreen
import com.flowmate.ui.screen.WeeklyHabitReportScreen
import com.flowmate.viewmodel.AuthViewModel
import com.flowmate.viewmodel.MonthlyHabitViewModel
import com.flowmate.viewmodel.MyHabitsViewModal
import com.flowmate.viewmodel.MyTasksViewModal
import com.flowmate.viewmodel.ReportsViewModel
import com.flowmate.viewmodel.SettingsViewModel
import com.flowmate.viewmodel.WeeklyHabitViewModel
import com.flowmate.viewmodel.YearlyHabitViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FlowMateNavGraph(settingsViewModel: com.flowmate.viewmodel.SettingsViewModel) {
    val navController = rememberNavController()

    val repository = remember {
        AuthRepository(
            userDao = database.userDao(),
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
    }
    val authViewModel = remember { AuthViewModel(repository) }

    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()
    val loading by authViewModel.loading.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val isLoggedIn = user != null

    // Navigation guard
    LaunchedEffect(isLoggedIn) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        android.util.Log.d("FlowMateNavGraph", "Current route: ${user?.userId}")
        if (isLoggedIn && currentRoute?.startsWith("auth") == true) {
            navController.navigate("main") {
                popUpTo("auth") { inclusive = true }
                launchSingleTop = true
            }
        } else if (!isLoggedIn && currentRoute?.startsWith("main") == true) {
            navController.navigate("auth") {
                popUpTo("main") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // UI Layer
    if (isLoggedIn) {
        val currentRoute = MainRoute.fromRoute(navBackStackEntry?.destination?.route)

        MainFrame(
            onNavigateTo = { route -> navController.navigate(route.route) },
            onLogout = { authViewModel.signOut() },
            currentRoute =currentRoute
        ) {
            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                mainNavGraph(navController, authViewModel, user, settingsViewModel)
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "auth"
        ) {
            authNavGraph(navController, authViewModel, loading, error)
        }
    }
}

private fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    loading: Boolean,
    error: String?
) {
    navigation(startDestination = AuthRoute.Login.route, route = "auth") {
        composable(AuthRoute.Login.route) {
            LoginScreen(
                onLogin = { u, p -> authViewModel.login(u.trim(), p) },
                onNavigateToSignUp = { navController.navigate(AuthRoute.SignUp.route) },
                loading = loading,
                error = error,
                viewModel = authViewModel
            )
        }
        composable(AuthRoute.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate(AuthRoute.Login.route) },
                onSignUp = { name, email, user, pass ->
                    authViewModel.register(name, email, user, pass)
                },
                loading = loading,
                error = error
            )
        }
    }
}

private fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    user: UserEntity?,
    settingsViewModel: com.flowmate.viewmodel.SettingsViewModel
) {
    navigation(startDestination = MainRoute.Home.route, route = "main") {

        composable(MainRoute.Home.route) {
            HomeScreen(
                modifier = Modifier,
                onNavigateTo = { route ->
                    navController.navigate(route.route) {
                        popUpTo(MainRoute.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                userName = user?.username ?: "",

            )
        }

        composable(MainRoute.Habits.route) {
            val myHabitViewModal = remember { MyHabitsViewModal() }
            val habits by myHabitViewModal.habits.collectAsState(initial = emptyList())
            val suggestions by myHabitViewModal.habitSuggestions.collectAsState(initial = emptyList())

            MyHabitsWithModalSheet(
                habits = habits,
                suggestions = suggestions,
                onToggleComplete = { habit -> myHabitViewModal.toggleHabitCompletion(habit) },
                onAddHabit = { habit -> myHabitViewModal.addHabit(habit) },
                navController = navController
            )
        }

        composable("weeklyHabitReport") {
            val viewModel: ReportsViewModel = viewModel()
            WeeklyHabitReportScreen(viewModel = viewModel)
        }

        composable("habitProgress") {
            val habitRepository = remember { com.flowmate.repository.HabitRepository() }
            val userId = user?.userId ?: ""
            val weeklyViewModel = remember { WeeklyHabitViewModel(habitRepository, userId) }
            val monthlyViewModel = remember { MonthlyHabitViewModel(habitRepository, userId) }
            val yearlyViewModel = remember { YearlyHabitViewModel(habitRepository, userId) }

            HabitProgressScreen(
                weeklyViewModel = weeklyViewModel,
                monthlyViewModel = monthlyViewModel,
                yearlyViewModel = yearlyViewModel
            )
        }

        composable(MainRoute.HabitProgress.route) {
            val habitRepository = remember { com.flowmate.repository.HabitRepository() }
            val userId = user?.userId ?: ""
            val weeklyViewModel = remember { WeeklyHabitViewModel(habitRepository, userId) }
            val monthlyViewModel = remember { MonthlyHabitViewModel(habitRepository, userId) }
            val yearlyViewModel = remember { YearlyHabitViewModel(habitRepository, userId) }
            HabitProgressScreen(
                weeklyViewModel = weeklyViewModel,
                monthlyViewModel = monthlyViewModel,
                yearlyViewModel = yearlyViewModel
            )
        }

        composable(MainRoute.Tasks.route) {
            val myTasksViewModal = remember { MyTasksViewModal() }
            val tasks by myTasksViewModal.tasks.collectAsState(initial = emptyList())
            val suggestions by myTasksViewModal.tasks.collectAsState(initial = emptyList())

            MyTasksScreen(
                tasks = tasks,
                onToggleTask = { task -> myTasksViewModal.toggleTaskCompletion(task) },
                onAddTask = { task -> myTasksViewModal.addTask(task) },
            )
        }

        composable(MainRoute.Calendar.route) {
            CalendarScreen()
        }

        composable(MainRoute.Chronometer.route) {
            ChronometerScreen()
        }

        composable(MainRoute.Reports.route) {
            val reportsViewModel: ReportsViewModel = viewModel()
            ReportsScreen(
                viewModel = reportsViewModel,
                entries = emptyList(),
                onEntryClick = {},
                onRefresh = {},
                weeklyProgress = 0.7f,
                monthlyProgress = 0.4f,
                yearlyProgress = 0.9f
            )
        }

        composable(MainRoute.Profile.route) {
            ProfileScreen(
                currentName = user?.username ?: "",
                onNameChange = { },
                onSaveName = { },
                onResetProgress = { },
                onExportData = { }
            )
        }

        composable(MainRoute.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateTo = { route ->
                    navController.navigate(route.route) {
                        popUpTo(MainRoute.Settings.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(MainRoute.EditCredentials.route) {
            EditCredentialsScreen(
                onSaveSuccess = { },
                onError = { }
            )
        }
    }
}
