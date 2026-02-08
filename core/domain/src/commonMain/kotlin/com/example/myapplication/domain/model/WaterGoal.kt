package com.example.myapplication.domain.model

data class WaterGoal(
    val dailyGoalMl: Int = 2000, // 기본 2L
    val reminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 60, // 1시간마다
    val reminderStartHour: Int = 8, // 오전 8시부터
    val reminderEndHour: Int = 22, // 오후 10시까지
    val cupSizeMl: Int = 250 // 기본 컵 사이즈
)
