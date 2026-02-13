package com.example.myapplication.domain.usecase.gamification

import com.example.myapplication.domain.model.DailyChallenge
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetTodayChallengeUseCase(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<DailyChallenge?> {
        return repository.getTodayChallenge()
    }
}
