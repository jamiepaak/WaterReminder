package com.example.myapplication

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.myapplication.feature.water.ui.WaterHomeScreen
import com.example.myapplication.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Navigator(WaterHomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
