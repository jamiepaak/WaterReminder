package com.example.myapplication.domain.usecase.water

import com.example.myapplication.domain.model.HourlyWaterIntake
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetHourlyIntakesUseCase(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(): Flow<List<HourlyWaterIntake>> {
        return waterRepository.getTodayHourlyIntakes()
    }
}
