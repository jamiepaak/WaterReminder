package com.example.myapplication.domain.model

enum class ChallengeType {
    DRINK_BEFORE_NOON,     // 정오 전 목표의 50% 마시기
    HOURLY_HYDRATION,      // 매시간 물 마시기 (8시간)
    MORNING_BOOST,         // 기상 후 1시간 내 500ml
    EVENING_WIND_DOWN,     // 저녁 6시 이전 목표 달성
    CONSISTENT_PORTIONS    // 250ml씩 8번 마시기
}

data class DailyChallenge(
    val type: ChallengeType,
    val title: String,
    val description: String,
    val icon: String,
    val expReward: Int,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isCompleted: Boolean = false,
    val date: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (targetValue > 0) (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f) else 0f
    
    companion object {
        fun generateDailyChallenge(dayOfWeek: Int): DailyChallenge {
            val challenges = listOf(
                DailyChallenge(
                    type = ChallengeType.DRINK_BEFORE_NOON,
                    title = "모닝 하이드레이션",
                    description = "정오 전에 목표량의 50% 마시기",
                    icon = "☀️",
                    expReward = 80,
                    targetValue = 1
                ),
                DailyChallenge(
                    type = ChallengeType.HOURLY_HYDRATION,
                    title = "시간당 한 잔",
                    description = "8시간 동안 매시간 물 마시기",
                    icon = "⏰",
                    expReward = 100,
                    targetValue = 8
                ),
                DailyChallenge(
                    type = ChallengeType.MORNING_BOOST,
                    title = "아침 부스트",
                    description = "기상 후 1시간 내 500ml 마시기",
                    icon = "🌄",
                    expReward = 60,
                    targetValue = 500
                ),
                DailyChallenge(
                    type = ChallengeType.EVENING_WIND_DOWN,
                    title = "저녁 여유",
                    description = "저녁 6시 이전 목표 달성",
                    icon = "🌆",
                    expReward = 90,
                    targetValue = 1
                ),
                DailyChallenge(
                    type = ChallengeType.CONSISTENT_PORTIONS,
                    title = "균등 배분",
                    description = "250ml씩 8번 나눠 마시기",
                    icon = "📊",
                    expReward = 70,
                    targetValue = 8
                )
            )
            
            return challenges[dayOfWeek % challenges.size]
        }
    }
}
