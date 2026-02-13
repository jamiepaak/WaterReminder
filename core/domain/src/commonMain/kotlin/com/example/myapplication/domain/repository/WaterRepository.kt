package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Achievement
import com.example.myapplication.domain.model.AchievementType
import com.example.myapplication.domain.model.DailyChallenge
import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.model.HourlyWaterIntake
import com.example.myapplication.domain.model.UserLevel
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
    
    // Gamification - Level & Exp
    fun getUserLevel(): Flow<UserLevel>
    suspend fun addExp(amount: Int)
    suspend fun getTotalExp(): Int
    
    // Gamification - Achievements
    fun getAchievements(): Flow<List<Achievement>>
    suspend fun unlockAchievement(type: AchievementType)
    suspend fun checkAndUnlockAchievements()
    
    // Gamification - Daily Challenge
    fun getTodayChallenge(): Flow<DailyChallenge?>
    suspend fun updateChallengeProgress(currentValue: Int)
    suspend fun completeChallenge()
}
