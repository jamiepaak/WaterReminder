package com.example.myapplication.feature.water.presentation

import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.presentation.mvi.UiEffect
import com.example.myapplication.presentation.mvi.UiEvent
import com.example.myapplication.presentation.mvi.UiState

data class WaterSettingsState(
    val goal: WaterGoal = WaterGoal(),
    val isLoading: Boolean = true
) : UiState

sealed interface WaterSettingsEvent : UiEvent {
    data object LoadSettings : WaterSettingsEvent
    data class UpdateDailyGoal(val goalMl: Int) : WaterSettingsEvent
    data class UpdateCupSize(val sizeMl: Int) : WaterSettingsEvent
    data class UpdateReminderEnabled(val enabled: Boolean) : WaterSettingsEvent
    data class UpdateReminderInterval(val minutes: Int) : WaterSettingsEvent
    data class UpdateReminderStartHour(val hour: Int) : WaterSettingsEvent
    data class UpdateReminderEndHour(val hour: Int) : WaterSettingsEvent
    data object SaveSettings : WaterSettingsEvent
    data object OnBackClick : WaterSettingsEvent
}

sealed interface WaterSettingsEffect : UiEffect {
    data object NavigateBack : WaterSettingsEffect
    data class ShowMessage(val message: String) : WaterSettingsEffect
}
