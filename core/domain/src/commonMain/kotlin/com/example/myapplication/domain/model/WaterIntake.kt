package com.example.myapplication.domain.model

import kotlinx.datetime.LocalDateTime

data class WaterIntake(
    val id: Long = 0,
    val amount: Int, // ml 단위
    val drankAt: LocalDateTime,
    val note: String? = null
)
