package com.example.myapplication

import androidx.compose.ui.window.ComposeUIViewController
import com.example.myapplication.di.initKoin
import com.example.myapplication.di.platformModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            modules(platformModule)
        }
        Napier.base(DebugAntilog())
    }
) {
    App()
}
