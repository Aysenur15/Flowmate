package com.flowmate.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.flowmate.FlowMateApp.Companion.database
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
fun FlowMateNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    // Room DB oluştur
    val repository = AuthRepository(
        userDao = database.userDao(),
        auth = FirebaseAuth.getInstance(),
        firestore = FirebaseFirestore.getInstance()
    )

    // ViewModel elle oluşturuluyor (manuel DI)
    val authViewModel = AuthViewModel(repository)
/*
    val db = remember {
        Room.databaseBuilder(
            context,
            FlowMateDatabase::class.java,
            "flowmate.db"
        ).build()
    }

    val authRepository = remember {
        AuthRepository(
            userDao = db.userDao(),
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
    }
*/

    val authState by authViewModel.authState.collectAsState()
    val isLoggedIn = authState.isAuthenticated

/*
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
            currentDestination = rememberCurrentRoute(navController)
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
    }*/
    if (isLoggedIn) {
        MainFrame(
            onNavigateTo = { route -> navController.navigate(route.route) },
            onLogout = { authViewModel.signOut() },
            currentDestination = rememberCurrentRoute(navController)
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
    val myTasksViewModal = MyTasksViewModal()
    val myHabitViewModal = MyHabitsViewModal()

    navigation(startDestination = MainRoute.Home.route, route = "main") {

        composable(MainRoute.Home.route) {
            val authState by authViewModel.authState.collectAsState()
            val userName = authState.userName ?: ""

            HomeScreen(
                modifier = Modifier,
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
                onAddHabit = { habit ->
                    myHabitViewModal.addHabit(habit)
                },
                navController = navController
            )
        }

        composable("weeklyHabitReport") {
            val viewModel: ReportsViewModel = viewModel()
            WeeklyHabitReportScreen(viewModel = viewModel)
        }

        composable("habitProgress") {
            val weeklyViewModel = remember { WeeklyHabitViewModel() }
            val monthlyViewModel = remember { MonthlyHabitViewModel() }
            val yearlyViewModel = remember { YearlyHabitViewModel() }

            HabitProgressScreen(
                weeklyViewModel = weeklyViewModel,
                monthlyViewModel = monthlyViewModel,
                yearlyViewModel = yearlyViewModel
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
                onAddTask = { /* Add task logic */ }
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
                currentName = authViewModel.authState.collectAsState().value.userName ?: "",
                onNameChange = { },
                onSaveName = { },
                onResetProgress = { },
                onExportData = { }
            )
        }

        composable(MainRoute.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateTo = { route ->
                    navController.navigate(route.route) {
                        popUpTo(MainRoute.Settings.route) { inclusive = true }
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
