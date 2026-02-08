package com.example.myapplication.di

import com.example.myapplication.data.datasource.local.DatabaseDriverFactory
import com.example.myapplication.notification.WaterReminderScheduler
import com.example.myapplication.notification.WaterReminderSchedulerImpl
import org.koin.dsl.module

val platformModule = module {
    single { DatabaseDriverFactory() }
    single<WaterReminderScheduler> { WaterReminderSchedulerImpl() }
}
