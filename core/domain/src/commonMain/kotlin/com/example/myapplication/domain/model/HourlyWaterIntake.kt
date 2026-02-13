package com.example.myapplication.domain.model

data class HourlyWaterIntake(
    val hour: Int,        // 0-23
    val totalAmount: Int, // ml
    val intakeCount: Int
)
