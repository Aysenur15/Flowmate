package com.flowmate.ui.component

sealed class AuthRoute(val route: String) {
    data object Login : AuthRoute("login")
    data object SignUp : AuthRoute("signup")
}

sealed class MainRoute(val route: String) {
    data object Home : MainRoute("home")
    data object Habits : MainRoute("my habits")
    data object Tasks : MainRoute("my tasks")
    data object Calendar : MainRoute("calendar")
    data object Chronometer : MainRoute("chronometer")
    data object Reports : MainRoute("reports")
    data object Profile : MainRoute("profile")
    data object Theme : MainRoute("theme")
    data object Achievements : MainRoute("achievements")
    data object Settings : MainRoute("settings")
}