package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.model.HourlyWaterIntake
import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.domain.model.WaterIntake
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface WaterRepository {
    // Water Intake
    fun getTodayIntakes(): Flow<List<WaterIntake>>
    fun getIntakesByDate(date: LocalDate): Flow<List<WaterIntake>>
    suspend fun addIntake(intake: WaterIntake)
    suspend fun deleteIntake(id: Long)

    // Daily Summary
    fun getTodaySummary(): Flow<DailyWaterSummary>
    fun getWeeklySummary(): Flow<List<DailyWaterSummary>>

    // Hourly Breakdown
    fun getTodayHourlyIntakes(): Flow<List<HourlyWaterIntake>>

    // Goal Settings
    fun getGoal(): Flow<WaterGoal>
    suspend fun updateGoal(goal: WaterGoal)
}
