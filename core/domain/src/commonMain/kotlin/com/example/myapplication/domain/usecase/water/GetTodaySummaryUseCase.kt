package com.example.myapplication.domain.usecase.water

import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetTodaySummaryUseCase(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(): Flow<DailyWaterSummary> {
        return waterRepository.getTodaySummary()
    }
}
