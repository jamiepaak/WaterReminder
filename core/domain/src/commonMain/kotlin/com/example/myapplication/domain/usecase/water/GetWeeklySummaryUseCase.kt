package com.example.myapplication.domain.usecase.water

import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetWeeklySummaryUseCase(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(): Flow<List<DailyWaterSummary>> {
        return waterRepository.getWeeklySummary()
    }
}
