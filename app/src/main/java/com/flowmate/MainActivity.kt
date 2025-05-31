package com.flowmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.flowmate.ui.component.FlowMateNavGraph
import com.flowmate.ui.theme.FlowMateTheme
import com.flowmate.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel: SettingsViewModel by viewModels()
        setContent {
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            FlowMateTheme(
                darkTheme = isDarkTheme
            ) {
                FlowMateNavGraph(settingsViewModel = settingsViewModel)
            }
        }
    }
}

