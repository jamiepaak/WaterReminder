package com.example.myapplication.domain.model

import kotlinx.datetime.LocalDate

data class DailyWaterSummary(
    val date: LocalDate,
    val totalAmount: Int, // ml
    val goalAmount: Int, // ml
    val intakeCount: Int
) {
    val progressPercent: Float
        get() = if (goalAmount > 0) {
            (totalAmount.toFloat() / goalAmount).coerceAtMost(1f)
        } else 0f

    val isGoalAchieved: Boolean
        get() = totalAmount >= goalAmount

    val remainingAmount: Int
        get() = (goalAmount - totalAmount).coerceAtLeast(0)
}
