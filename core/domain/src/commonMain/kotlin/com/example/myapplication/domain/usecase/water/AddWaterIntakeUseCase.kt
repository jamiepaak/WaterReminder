package com.example.myapplication.domain.usecase.water

import com.example.myapplication.domain.model.WaterIntake
import com.example.myapplication.domain.repository.WaterRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddWaterIntakeUseCase(
    private val waterRepository: WaterRepository
) {
    suspend operator fun invoke(amountMl: Int, note: String? = null) {
        val intake = WaterIntake(
            amount = amountMl,
            drankAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            note = note
        )
        waterRepository.addIntake(intake)
    }
}
