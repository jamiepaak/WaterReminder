package com.example.myapplication.domain.model

enum class AchievementType {
    FIRST_DRINK,           // 첫 물 마시기
    GOAL_ACHIEVED_1,       // 목표 1회 달성
    GOAL_ACHIEVED_7,       // 목표 7회 연속 달성
    GOAL_ACHIEVED_30,      // 목표 30회 연속 달성
    TOTAL_WATER_10L,       // 총 10L 마시기
    TOTAL_WATER_100L,      // 총 100L 마시기
    TOTAL_WATER_1000L,     // 총 1000L 마시기
    EARLY_BIRD,            // 오전 6시 전 첫 물 마시기
    NIGHT_OWL,             // 밤 10시 이후 물 마시기
    CONSISTENT_WEEK,       // 일주일 연속 목표 달성
    HYDRATION_MASTER       // 레벨 10 달성
}

data class Achievement(
    val type: AchievementType,
    val title: String,
    val description: String,
    val icon: String,
    val expReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
) {
    companion object {
        fun getAllAchievements(): List<Achievement> = listOf(
            Achievement(
                type = AchievementType.FIRST_DRINK,
                title = "첫 시작",
                description = "첫 물 마시기 기록",
                icon = "💧",
                expReward = 10
            ),
            Achievement(
                type = AchievementType.GOAL_ACHIEVED_1,
                title = "목표 달성",
                description = "하루 목표량 달성",
                icon = "🎯",
                expReward = 50
            ),
            Achievement(
                type = AchievementType.GOAL_ACHIEVED_7,
                title = "일주일 챔피언",
                description = "7일 연속 목표 달성",
                icon = "🏆",
                expReward = 200
            ),
            Achievement(
                type = AchievementType.GOAL_ACHIEVED_30,
                title = "한 달의 기적",
                description = "30일 연속 목표 달성",
                icon = "👑",
                expReward = 500
            ),
            Achievement(
                type = AchievementType.TOTAL_WATER_10L,
                title = "물 애호가",
                description = "총 10L 마시기",
                icon = "💦",
                expReward = 100
            ),
            Achievement(
                type = AchievementType.TOTAL_WATER_100L,
                title = "수분 전문가",
                description = "총 100L 마시기",
                icon = "🌊",
                expReward = 300
            ),
            Achievement(
                type = AchievementType.TOTAL_WATER_1000L,
                title = "워터 마스터",
                description = "총 1000L 마시기",
                icon = "🌟",
                expReward = 1000
            ),
            Achievement(
                type = AchievementType.EARLY_BIRD,
                title = "아침 새",
                description = "오전 6시 전 물 마시기",
                icon = "🌅",
                expReward = 30
            ),
            Achievement(
                type = AchievementType.NIGHT_OWL,
                title = "야행성",
                description = "밤 10시 이후 물 마시기",
                icon = "🌙",
                expReward = 30
            ),
            Achievement(
                type = AchievementType.CONSISTENT_WEEK,
                title = "꾸준함의 힘",
                description = "일주일 연속 기록",
                icon = "💪",
                expReward = 150
            ),
            Achievement(
                type = AchievementType.HYDRATION_MASTER,
                title = "수분왕",
                description = "레벨 10 달성",
                icon = "👑",
                expReward = 500
            )
        )
    }
}
