package com.example.myapplication.data.repository

import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.domain.model.DailyWaterSummary
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
}
