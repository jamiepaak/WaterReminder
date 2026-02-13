package com.example.myapplication.feature.water.presentation

import com.example.myapplication.domain.usecase.water.GetWaterGoalUseCase
import com.example.myapplication.domain.usecase.water.GetWeeklySummaryUseCase
import com.example.myapplication.presentation.base.BaseScreenModel

class WaterWeeklyReportScreenModel(
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
    private val getWaterGoalUseCase: GetWaterGoalUseCase
) : BaseScreenModel<WaterWeeklyReportState, WaterWeeklyReportEvent, WaterWeeklyReportEffect>(
    WaterWeeklyReportState()
) {

    init {
        loadData()
    }

    override fun handleEvent(event: WaterWeeklyReportEvent) {
        when (event) {
            is WaterWeeklyReportEvent.LoadData -> loadData()
            is WaterWeeklyReportEvent.OnBackClick -> sendEffect(WaterWeeklyReportEffect.NavigateBack)
        }
    }

    private fun loadData() {
        launchScope {
            updateState { copy(isLoading = true) }
            getWaterGoalUseCase().collect { goal ->
                updateState { copy(goal = goal) }
            }
        }
        launchScope {
            getWeeklySummaryUseCase().collect { weekly ->
                updateState { copy(weeklySummary = weekly, isLoading = false) }
            }
        }
    }
}
