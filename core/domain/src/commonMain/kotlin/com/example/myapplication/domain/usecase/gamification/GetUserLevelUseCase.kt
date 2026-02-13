package com.example.myapplication.domain.usecase.gamification

import com.example.myapplication.domain.model.UserLevel
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetUserLevelUseCase(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<UserLevel> {
        return repository.getUserLevel()
    }
}
