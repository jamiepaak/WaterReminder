package com.example.myapplication.domain.usecase.gamification

import com.example.myapplication.domain.model.Achievement
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetAchievementsUseCase(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<List<Achievement>> {
        return repository.getAchievements()
    }
}
