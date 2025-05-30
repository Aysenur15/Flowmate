package com.flowmate.ui.component

sealed class AuthRoute(val route: String) {
    data object Login : AuthRoute("login")
    data object SignUp : AuthRoute("signup")
}

sealed class MainRoute(val route: String, val title: String) {
    data object Home : MainRoute("home", "Home")
    data object Habits : MainRoute("my habits", "My Habits")
    data object Tasks : MainRoute("my tasks", "My Tasks")
    data object Calendar : MainRoute("calendar", "Calendar")
    data object Chronometer : MainRoute("chronometer", "Chronometer")
    data object Reports : MainRoute("reports", "Reports")
    data object Profile : MainRoute("profile", "Profile")
    data object Theme : MainRoute("theme", "Theme")
    data object Achievements : MainRoute("achievements", "Achievements")
    data object Settings : MainRoute("settings", "Settings")
    data object EditCredentials : MainRoute("edit_credentials", "Edit Credentials")

    companion object {
        fun fromRoute(route: String?): MainRoute {
            return when (route) {
                Home.route -> Home
                Habits.route -> Habits
                Tasks.route -> Tasks
                Calendar.route -> Calendar
                Chronometer.route -> Chronometer
                Reports.route -> Reports
                Profile.route -> Profile
                Theme.route -> Theme
                Achievements.route -> Achievements
                Settings.route -> Settings
                EditCredentials.route -> EditCredentials
                else -> Home
            }
        }
    }
}

