package com.flowmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.flowmate.ui.component.FlowMateNavGraph
import com.flowmate.ui.theme.FlowMateTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            FlowMateTheme {
                FlowMateNavGraph()
            }
        }


    }
}