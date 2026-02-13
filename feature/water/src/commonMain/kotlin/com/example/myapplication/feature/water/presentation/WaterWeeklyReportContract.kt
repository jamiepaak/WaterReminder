package com.example.myapplication.feature.water.presentation

import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.presentation.mvi.UiEffect
import com.example.myapplication.presentation.mvi.UiEvent
import com.example.myapplication.presentation.mvi.UiState

data class WaterWeeklyReportState(
    val weeklySummary: List<DailyWaterSummary> = emptyList(),
    val goal: WaterGoal = WaterGoal(),
    val isLoading: Boolean = true
) : UiState {
    val averageDailyIntake: Int
        get() = if (weeklySummary.isNotEmpty())
            weeklySummary.sumOf { it.totalAmount } / weeklySummary.size
        else 0

    val totalWeeklyIntake: Int
        get() = weeklySummary.sumOf { it.totalAmount }

    val goalAchievedDays: Int
        get() = weeklySummary.count { it.isGoalAchieved }

    val bestDay: DailyWaterSummary?
        get() = weeklySummary.maxByOrNull { it.totalAmount }

    val achievementRate: Float
        get() = if (weeklySummary.isNotEmpty())
            goalAchievedDays.toFloat() / weeklySummary.size
        else 0f
}

sealed interface WaterWeeklyReportEvent : UiEvent {
    data object LoadData : WaterWeeklyReportEvent
    data object OnBackClick : WaterWeeklyReportEvent
}

sealed interface WaterWeeklyReportEffect : UiEffect {
    data object NavigateBack : WaterWeeklyReportEffect
}
