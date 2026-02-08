package com.example.myapplication.domain.usecase.water

import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.domain.repository.WaterRepository

class UpdateWaterGoalUseCase(
    private val waterRepository: WaterRepository
) {
    suspend operator fun invoke(goal: WaterGoal) {
        waterRepository.updateGoal(goal)
    }
}
