package com.example.routefitnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.routefitnative.ui.screens.login.LoginScreen
import com.example.routefitnative.ui.theme.RouteFitNativeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RouteFitNativeTheme {
                LoginScreen()
            }
        }
    }
}
