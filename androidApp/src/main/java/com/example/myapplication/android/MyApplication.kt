package com.example.myapplication.android

import android.app.Application
import com.example.myapplication.di.initKoin
import com.example.myapplication.di.platformModule
import com.google.android.gms.ads.MobileAds
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        MobileAds.initialize(this) {}

        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(platformModule)
        }
    }
}
