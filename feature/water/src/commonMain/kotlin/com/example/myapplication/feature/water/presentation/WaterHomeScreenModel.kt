package com.example.myapplication.feature.water.presentation

import com.example.myapplication.domain.usecase.gamification.AddExpUseCase
import com.example.myapplication.domain.usecase.gamification.GetAchievementsUseCase
import com.example.myapplication.domain.usecase.gamification.GetTodayChallengeUseCase
import com.example.myapplication.domain.usecase.gamification.GetUserLevelUseCase
import com.example.myapplication.domain.usecase.water.AddWaterIntakeUseCase
import com.example.myapplication.domain.usecase.water.GetHourlyIntakesUseCase
import com.example.myapplication.domain.usecase.water.GetTodaySummaryUseCase
import com.example.myapplication.domain.usecase.water.GetWaterGoalUseCase
import com.example.myapplication.domain.usecase.water.GetWeeklySummaryUseCase
import com.example.myapplication.domain.repository.WaterRepository
import com.example.myapplication.presentation.base.BaseScreenModel

class WaterHomeScreenModel(
    private val getTodaySummaryUseCase: GetTodaySummaryUseCase,
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
    private val getHourlyIntakesUseCase: GetHourlyIntakesUseCase,
    private val getWaterGoalUseCase: GetWaterGoalUseCase,
    private val addWaterIntakeUseCase: AddWaterIntakeUseCase,
    private val getUserLevelUseCase: GetUserLevelUseCase,
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val getTodayChallengeUseCase: GetTodayChallengeUseCase,
    private val addExpUseCase: AddExpUseCase,
    private val waterRepository: WaterRepository
) : BaseScreenModel<WaterHomeState, WaterHomeEvent, WaterHomeEffect>(WaterHomeState()) {

    init {
        loadData()
    }

    override fun handleEvent(event: WaterHomeEvent) {
        when (event) {
            is WaterHomeEvent.LoadData -> loadData()
            is WaterHomeEvent.AddWater -> addWater(event.amountMl)
            is WaterHomeEvent.DeleteIntake -> deleteIntake(event.id)
            is WaterHomeEvent.ShowAddDialog -> updateState { copy(showAddDialog = true) }
            is WaterHomeEvent.HideAddDialog -> updateState { copy(showAddDialog = false, customAmount = "") }
            is WaterHomeEvent.UpdateCustomAmount -> updateState { copy(customAmount = event.amount) }
            is WaterHomeEvent.AddCustomAmount -> addCustomAmount()
            is WaterHomeEvent.NavigateToSettings -> sendEffect(WaterHomeEffect.NavigateToSettings)
            is WaterHomeEvent.NavigateToHistory -> sendEffect(WaterHomeEffect.NavigateToHistory)
            is WaterHomeEvent.NavigateToAchievements -> sendEffect(WaterHomeEffect.NavigateToAchievements)
        }
    }

    private fun loadData() {
        launchScope {
            updateState { copy(isLoading = true) }

            // Load goal
            getWaterGoalUseCase().collect { goal ->
                updateState { copy(goal = goal) }
            }
        }

        launchScope {
            getTodaySummaryUseCase().collect { summary ->
                updateState { copy(todaySummary = summary, isLoading = false) }
            }
        }

        launchScope {
            waterRepository.getTodayIntakes().collect { intakes ->
                updateState { copy(todayIntakes = intakes) }
            }
        }

        launchScope {
            getWeeklySummaryUseCase().collect { weekly ->
                updateState { copy(weeklySummary = weekly) }
            }
        }

        launchScope {
            getHourlyIntakesUseCase().collect { hourly ->
                updateState { copy(hourlyIntakes = hourly) }
            }
        }
        
        // Load gamification data
        launchScope {
            getUserLevelUseCase().collect { level ->
                updateState { copy(userLevel = level) }
            }
        }
        
        launchScope {
            getTodayChallengeUseCase().collect { challenge ->
                updateState { copy(todayChallenge = challenge) }
            }
        }
        
        launchScope {
            getAchievementsUseCase().collect { achievements ->
                val recent = achievements.filter { it.isUnlocked }.sortedByDescending { it.unlockedAt }.take(3)
                updateState { copy(recentAchievements = recent) }
            }
        }
    }

    private fun addWater(amountMl: Int) {
        launchScope {
            addWaterIntakeUseCase(amountMl)
            
            // 경험치 추가 (물 1잔 = 10 EXP)
            addExpUseCase(10)
            
            // 배지 체크
            waterRepository.checkAndUnlockAchievements()
            
            // 목표 달성 체크
            val summary = currentState.todaySummary
            if (summary != null && summary.totalAmount + amountMl >= summary.goalAmount) {
                // 목표 달성 시 보너스 경험치
                addExpUseCase(50)
                waterRepository.unlockAchievement(com.example.myapplication.domain.model.AchievementType.GOAL_ACHIEVED_1)
            }
            
            sendEffect(WaterHomeEffect.ShowMessage("${amountMl}ml 추가됨 +10 EXP"))
            loadData()
        }
    }

    private fun deleteIntake(id: Long) {
        launchScope {
            waterRepository.deleteIntake(id)
            loadData()
        }
    }

    private fun addCustomAmount() {
        val amount = currentState.customAmount.toIntOrNull()
        if (amount != null && amount > 0) {
            addWater(amount)
            updateState { copy(showAddDialog = false, customAmount = "") }
        }
    }
}
