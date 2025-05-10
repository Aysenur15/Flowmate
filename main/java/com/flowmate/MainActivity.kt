package com.flowmate

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import com.flowmate.ui.component.FlowMateNavGraph
import com.flowmate.ui.theme.FlowMateTheme
import com.flowmate.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    // ViewModel initialization can be done here if needed
    private val authViewModel: AuthViewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set the status bar color to transparent
        window.statusBarColor = Color.Transparent.value.toInt()
        // Set the navigation bar color to transparent
        window.navigationBarColor = Color.Transparent.value.toInt()
        // Set the system UI visibility to light mode
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR


        // Set the content view to the LoginScreen
        setContent {
            FlowMateTheme {
                FlowMateNavGraph(
                    authViewModel = authViewModel,
                )

            }
        }
    }
}