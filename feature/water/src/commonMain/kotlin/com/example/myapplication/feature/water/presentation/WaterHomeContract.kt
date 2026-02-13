package com.example.myapplication.feature.water.presentation

import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.model.HourlyWaterIntake
import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.domain.model.WaterIntake
import com.example.myapplication.presentation.mvi.UiEffect
import com.example.myapplication.presentation.mvi.UiEvent
import com.example.myapplication.presentation.mvi.UiState

data class WaterHomeState(
    val todaySummary: DailyWaterSummary? = null,
    val todayIntakes: List<WaterIntake> = emptyList(),
    val weeklySummary: List<DailyWaterSummary> = emptyList(),
    val hourlyIntakes: List<HourlyWaterIntake> = emptyList(),
    val goal: WaterGoal = WaterGoal(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val customAmount: String = ""
) : UiState

sealed interface WaterHomeEvent : UiEvent {
    data object LoadData : WaterHomeEvent
    data class AddWater(val amountMl: Int) : WaterHomeEvent
    data class DeleteIntake(val id: Long) : WaterHomeEvent
    data object ShowAddDialog : WaterHomeEvent
    data object HideAddDialog : WaterHomeEvent
    data class UpdateCustomAmount(val amount: String) : WaterHomeEvent
    data object AddCustomAmount : WaterHomeEvent
    data object NavigateToSettings : WaterHomeEvent
    data object NavigateToHistory : WaterHomeEvent
}

sealed interface WaterHomeEffect : UiEffect {
    data object NavigateToSettings : WaterHomeEffect
    data object NavigateToHistory : WaterHomeEffect
    data class ShowMessage(val message: String) : WaterHomeEffect
}
