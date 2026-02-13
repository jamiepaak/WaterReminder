package com.example.myapplication.di

import com.example.myapplication.data.repository.WaterRepositoryImpl
import com.example.myapplication.domain.repository.WaterRepository
import com.example.myapplication.domain.usecase.water.AddWaterIntakeUseCase
import com.example.myapplication.domain.usecase.water.GetHourlyIntakesUseCase
import com.example.myapplication.domain.usecase.water.GetTodaySummaryUseCase
import com.example.myapplication.domain.usecase.water.GetWaterGoalUseCase
import com.example.myapplication.domain.usecase.water.GetWeeklySummaryUseCase
import com.example.myapplication.domain.usecase.water.UpdateWaterGoalUseCase
import com.example.myapplication.feature.water.presentation.WaterHomeScreenModel
import com.example.myapplication.feature.water.presentation.WaterSettingsScreenModel
import com.example.myapplication.feature.water.presentation.WaterWeeklyReportScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val waterModule = module {
    // Repository
    single<WaterRepository> { WaterRepositoryImpl(get()) }

    // UseCases
    factoryOf(::AddWaterIntakeUseCase)
    factoryOf(::GetTodaySummaryUseCase)
    factoryOf(::GetWeeklySummaryUseCase)
    factoryOf(::GetHourlyIntakesUseCase)
    factoryOf(::GetWaterGoalUseCase)
    factoryOf(::UpdateWaterGoalUseCase)

    // ScreenModels
    factoryOf(::WaterHomeScreenModel)
    factoryOf(::WaterSettingsScreenModel)
    factoryOf(::WaterWeeklyReportScreenModel)
}
