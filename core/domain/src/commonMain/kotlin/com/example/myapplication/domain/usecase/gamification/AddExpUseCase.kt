package com.example.myapplication.domain.usecase.gamification

import com.example.myapplication.domain.repository.WaterRepository

class AddExpUseCase(
    private val repository: WaterRepository
) {
    suspend operator fun invoke(amount: Int) {
        repository.addExp(amount)
    }
}
