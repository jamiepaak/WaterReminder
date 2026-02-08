package com.example.myapplication.feature.water.presentation

import com.example.myapplication.domain.usecase.water.GetWaterGoalUseCase
import com.example.myapplication.domain.usecase.water.UpdateWaterGoalUseCase
import com.example.myapplication.notification.WaterReminderScheduler
import com.example.myapplication.presentation.base.BaseScreenModel

class WaterSettingsScreenModel(
    private val getWaterGoalUseCase: GetWaterGoalUseCase,
    private val updateWaterGoalUseCase: UpdateWaterGoalUseCase,
    private val reminderScheduler: WaterReminderScheduler
) : BaseScreenModel<WaterSettingsState, WaterSettingsEvent, WaterSettingsEffect>(WaterSettingsState()) {

    init {
        loadSettings()
    }

    override fun handleEvent(event: WaterSettingsEvent) {
        when (event) {
            is WaterSettingsEvent.LoadSettings -> loadSettings()
            is WaterSettingsEvent.UpdateDailyGoal -> updateState {
                copy(goal = goal.copy(dailyGoalMl = event.goalMl))
            }
            is WaterSettingsEvent.UpdateCupSize -> updateState {
                copy(goal = goal.copy(cupSizeMl = event.sizeMl))
            }
            is WaterSettingsEvent.UpdateReminderEnabled -> updateState {
                copy(goal = goal.copy(reminderEnabled = event.enabled))
            }
            is WaterSettingsEvent.UpdateReminderInterval -> updateState {
                copy(goal = goal.copy(reminderIntervalMinutes = event.minutes))
            }
            is WaterSettingsEvent.UpdateReminderStartHour -> updateState {
                copy(goal = goal.copy(reminderStartHour = event.hour))
            }
            is WaterSettingsEvent.UpdateReminderEndHour -> updateState {
                copy(goal = goal.copy(reminderEndHour = event.hour))
            }
            is WaterSettingsEvent.SaveSettings -> saveSettings()
            is WaterSettingsEvent.OnBackClick -> sendEffect(WaterSettingsEffect.NavigateBack)
        }
    }

    private fun loadSettings() {
        launchScope {
            getWaterGoalUseCase().collect { goal ->
                updateState { copy(goal = goal, isLoading = false) }
            }
        }
    }

    private fun saveSettings() {
        launchScope {
            val goal = currentState.goal
            updateWaterGoalUseCase(goal)

            // 알림 스케줄 업데이트
            if (goal.reminderEnabled) {
                reminderScheduler.scheduleReminder(
                    intervalMinutes = goal.reminderIntervalMinutes,
                    startHour = goal.reminderStartHour,
                    endHour = goal.reminderEndHour
                )
            } else {
                reminderScheduler.cancelReminder()
            }

            sendEffect(WaterSettingsEffect.ShowMessage("설정이 저장되었습니다"))
            sendEffect(WaterSettingsEffect.NavigateBack)
        }
    }
}
