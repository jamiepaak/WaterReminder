package com.example.myapplication.feature.water.presentation

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
    }

    private fun addWater(amountMl: Int) {
        launchScope {
            addWaterIntakeUseCase(amountMl)
            sendEffect(WaterHomeEffect.ShowMessage("${amountMl}ml 추가됨"))
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
