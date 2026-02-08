package com.example.myapplication.domain.usecase.water

import com.example.myapplication.domain.model.WaterGoal
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetWaterGoalUseCase(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(): Flow<WaterGoal> {
        return waterRepository.getGoal()
    }
}
