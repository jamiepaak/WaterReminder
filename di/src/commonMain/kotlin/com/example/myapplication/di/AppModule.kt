package com.example.myapplication.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModules())
}

fun commonModules(): List<Module> = listOf(
    dataModule,
    waterModule
)
