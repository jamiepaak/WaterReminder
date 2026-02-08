package com.example.myapplication.di

import com.example.myapplication.data.local.AppDatabase
import org.koin.dsl.module

val dataModule = module {
    // Database
    single { AppDatabase(get<com.example.myapplication.data.datasource.local.DatabaseDriverFactory>().createDriver()) }
}
