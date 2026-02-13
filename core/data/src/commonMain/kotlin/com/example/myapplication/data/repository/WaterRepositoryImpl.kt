package com.example.myapplication.data.repository

import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.domain.model.Achievement
import com.example.myapplication.domain.model.AchievementType
import com.example.myapplication.domain.model.DailyChallenge
import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.model.HourlyWaterIntake
import com.example.myapplication.domain.model.UserLevel
import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.domain.model.WaterIntake
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class WaterRepositoryImpl(
    private val database: AppDatabase
) : WaterRepository {

    private val waterIntakeQueries = database.waterIntakeQueries
    private val waterGoalQueries = database.waterGoalQueries
    private val userLevelQueries = database.userLevelQueries
    private val achievementQueries = database.achievementQueries
    private val dailyChallengeQueries = database.dailyChallengeQueries

    override fun getTodayIntakes(): Flow<List<WaterIntake>> = flow {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val intakes = withContext(Dispatchers.IO) {
            waterIntakeQueries.selectByDate(today).executeAsList().map { entity ->
                WaterIntake(
                    id = entity.id,
                    amount = entity.amount.toInt(),
                    drankAt = LocalDateTime.parse(entity.drankAt),
                    note = entity.note
                )
            }
        }
        emit(intakes)
    }

    override fun getIntakesByDate(date: LocalDate): Flow<List<WaterIntake>> = flow {
        val intakes = withContext(Dispatchers.IO) {
            waterIntakeQueries.selectByDate(date.toString()).executeAsList().map { entity ->
                WaterIntake(
                    id = entity.id,
                    amount = entity.amount.toInt(),
                    drankAt = LocalDateTime.parse(entity.drankAt),
                    note = entity.note
                )
            }
        }
        emit(intakes)
    }

    override suspend fun addIntake(intake: WaterIntake) = withContext(Dispatchers.IO) {
        waterIntakeQueries.insert(
            amount = intake.amount.toLong(),
            drankAt = intake.drankAt.toString(),
            note = intake.note
        )
    }

    override suspend fun deleteIntake(id: Long) = withContext(Dispatchers.IO) {
        waterIntakeQueries.deleteById(id)
    }

    override fun getTodaySummary(): Flow<DailyWaterSummary> = flow {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val goal = withContext(Dispatchers.IO) {
            waterGoalQueries.getGoal().executeAsOne()
        }
        val totalAmount = withContext(Dispatchers.IO) {
            waterIntakeQueries.getTodayTotal().executeAsOne().toInt()
        }
        val intakeCount = withContext(Dispatchers.IO) {
            waterIntakeQueries.getTodayCount().executeAsOne().toInt()
        }

        emit(
            DailyWaterSummary(
                date = today,
                totalAmount = totalAmount,
                goalAmount = goal.dailyGoalMl.toInt(),
                intakeCount = intakeCount
            )
        )
    }

    override fun getWeeklySummary(): Flow<List<DailyWaterSummary>> = flow {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val weekAgo = today.minus(DatePeriod(days = 6))

        val goal = withContext(Dispatchers.IO) {
            waterGoalQueries.getGoal().executeAsOne()
        }

        val dailySummaries = withContext(Dispatchers.IO) {
            waterIntakeQueries.getDailySummary(
                weekAgo.toString(),
                today.toString()
            ).executeAsList()
        }

        val summaryMap = dailySummaries.associate { summary ->
            LocalDate.parse(summary.date!!) to DailyWaterSummary(
                date = LocalDate.parse(summary.date),
                totalAmount = summary.total?.toInt() ?: 0,
                goalAmount = goal.dailyGoalMl.toInt(),
                intakeCount = summary.count.toInt()
            )
        }

        // 지난 7일간의 데이터 생성 (없는 날은 0으로)
        val result = (0..6).map { daysAgo ->
            val date = today.minus(DatePeriod(days = daysAgo))
            summaryMap[date] ?: DailyWaterSummary(
                date = date,
                totalAmount = 0,
                goalAmount = goal.dailyGoalMl.toInt(),
                intakeCount = 0
            )
        }.reversed()

        emit(result)
    }

    override fun getTodayHourlyIntakes(): Flow<List<HourlyWaterIntake>> = flow {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val hourlyData = withContext(Dispatchers.IO) {
            waterIntakeQueries.getHourlyBreakdown(today).executeAsList().map { row ->
                HourlyWaterIntake(
                    hour = row.hour?.toInt() ?: 0,
                    totalAmount = row.totalAmount.toInt(),
                    intakeCount = row.intakeCount.toInt()
                )
            }
        }
        // 24시간 전체를 채워서 반환 (데이터 없는 시간은 0)
        val hourlyMap = hourlyData.associateBy { it.hour }
        val fullDay = (0..23).map { hour ->
            hourlyMap[hour] ?: HourlyWaterIntake(hour = hour, totalAmount = 0, intakeCount = 0)
        }
        emit(fullDay)
    }

    override fun getGoal(): Flow<WaterGoal> = flow {
        val entity = withContext(Dispatchers.IO) {
            waterGoalQueries.getGoal().executeAsOne()
        }
        emit(
            WaterGoal(
                dailyGoalMl = entity.dailyGoalMl.toInt(),
                reminderEnabled = entity.reminderEnabled == 1L,
                reminderIntervalMinutes = entity.reminderIntervalMinutes.toInt(),
                reminderStartHour = entity.reminderStartHour.toInt(),
                reminderEndHour = entity.reminderEndHour.toInt(),
                cupSizeMl = entity.cupSizeMl.toInt()
            )
        )
    }

    override suspend fun updateGoal(goal: WaterGoal) = withContext(Dispatchers.IO) {
        waterGoalQueries.updateGoal(
            dailyGoalMl = goal.dailyGoalMl.toLong(),
            reminderEnabled = if (goal.reminderEnabled) 1L else 0L,
            reminderIntervalMinutes = goal.reminderIntervalMinutes.toLong(),
            reminderStartHour = goal.reminderStartHour.toLong(),
            reminderEndHour = goal.reminderEndHour.toLong(),
            cupSizeMl = goal.cupSizeMl.toLong()
        )
    }
    
    // Gamification - Level & Exp
    override fun getUserLevel(): Flow<UserLevel> = flow {
        val totalExp = withContext(Dispatchers.IO) {
            userLevelQueries.getTotalExp().executeAsOne().toInt()
        }
        emit(UserLevel.fromTotalExp(totalExp))
    }
    
    override suspend fun addExp(amount: Int) = withContext(Dispatchers.IO) {
        userLevelQueries.addExp(amount.toLong())
    }
    
    override suspend fun getTotalExp(): Int = withContext(Dispatchers.IO) {
        userLevelQueries.getTotalExp().executeAsOne().toInt()
    }
    
    // Gamification - Achievements
    override fun getAchievements(): Flow<List<Achievement>> = flow {
        val unlockedMap = withContext(Dispatchers.IO) {
            achievementQueries.getAll().executeAsList()
                .associate { entity ->
                    AchievementType.valueOf(entity.type) to (entity.isUnlocked == 1L to entity.unlockedAt)
                }
        }
        
        val achievements = Achievement.getAllAchievements().map { achievement ->
            val (isUnlocked, unlockedAt) = unlockedMap[achievement.type] ?: (false to null)
            achievement.copy(
                isUnlocked = isUnlocked,
                unlockedAt = unlockedAt?.let { 
                    LocalDateTime.parse(it).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                }
            )
        }
        emit(achievements)
    }
    
    override suspend fun unlockAchievement(type: AchievementType) = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        achievementQueries.unlockAchievement(type.name, now)
        
        // 배지 획득 시 경험치 보상
        val achievement = Achievement.getAllAchievements().find { it.type == type }
        achievement?.let { addExp(it.expReward) }
    }
    
    override suspend fun checkAndUnlockAchievements() = withContext(Dispatchers.IO) {
        // 첫 물 마시기
        val totalIntakes = waterIntakeQueries.selectAll().executeAsList().size
        if (totalIntakes == 1) {
            unlockAchievement(AchievementType.FIRST_DRINK)
        }
        
        // 총 물 마신 양 체크
        val totalMl = waterIntakeQueries.selectAll().executeAsList().sumOf { it.amount }.toInt()
        when {
            totalMl >= 1_000_000 -> unlockAchievement(AchievementType.TOTAL_WATER_1000L)
            totalMl >= 100_000 -> unlockAchievement(AchievementType.TOTAL_WATER_100L)
            totalMl >= 10_000 -> unlockAchievement(AchievementType.TOTAL_WATER_10L)
        }
        
        // 레벨 10 달성
        val totalExp = getTotalExp()
        val level = UserLevel.fromTotalExp(totalExp).level
        if (level >= 10) {
            unlockAchievement(AchievementType.HYDRATION_MASTER)
        }
    }
    
    // Gamification - Daily Challenge
    override fun getTodayChallenge(): Flow<DailyChallenge?> = flow {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        
        val entity = withContext(Dispatchers.IO) {
            dailyChallengeQueries.getToday().executeAsOneOrNull()
        }
        
        if (entity == null) {
            // 오늘 챌린지가 없으면 새로 생성
            val dayOfWeek = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.value
            val newChallenge = DailyChallenge.generateDailyChallenge(dayOfWeek)
            withContext(Dispatchers.IO) {
                dailyChallengeQueries.insert(
                    date = today,
                    type = newChallenge.type.name,
                    currentValue = 0L,
                    isCompleted = 0L
                )
            }
            emit(newChallenge)
        } else {
            // 기존 챌린지 반환
            val dayOfWeek = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.value
            val baseChallenge = DailyChallenge.generateDailyChallenge(dayOfWeek)
            emit(
                baseChallenge.copy(
                    currentValue = entity.currentValue.toInt(),
                    isCompleted = entity.isCompleted == 1L
                )
            )
        }
    }
    
    override suspend fun updateChallengeProgress(currentValue: Int) = withContext(Dispatchers.IO) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        dailyChallengeQueries.updateProgress(currentValue.toLong(), today)
    }
    
    override suspend fun completeChallenge() = withContext(Dispatchers.IO) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        dailyChallengeQueries.completeChallenge(today)
        
        // 챌린지 완료 시 경험치 보상
        val challenge = dailyChallengeQueries.getToday().executeAsOneOrNull()
        challenge?.let {
            val dayOfWeek = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.value
            val baseChallenge = DailyChallenge.generateDailyChallenge(dayOfWeek)
            addExp(baseChallenge.expReward)
        }
    }
}
