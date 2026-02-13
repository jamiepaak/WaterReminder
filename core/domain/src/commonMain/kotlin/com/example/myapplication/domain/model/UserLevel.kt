package com.example.myapplication.domain.model

data class UserLevel(
    val level: Int = 1,
    val currentExp: Int = 0,
    val expToNextLevel: Int = 100,
    val totalExp: Int = 0
) {
    val progress: Float
        get() = if (expToNextLevel > 0) currentExp.toFloat() / expToNextLevel.toFloat() else 0f
    
    companion object {
        fun calculateExpForLevel(level: Int): Int {
            return level * 100
        }
        
        fun fromTotalExp(totalExp: Int): UserLevel {
            var level = 1
            var remainingExp = totalExp
            
            while (remainingExp >= calculateExpForLevel(level)) {
                remainingExp -= calculateExpForLevel(level)
                level++
            }
            
            return UserLevel(
                level = level,
                currentExp = remainingExp,
                expToNextLevel = calculateExpForLevel(level),
                totalExp = totalExp
            )
        }
    }
}
