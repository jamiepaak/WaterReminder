package com.example.myapplication.notification

interface WaterReminderScheduler {
    fun scheduleReminder(intervalMinutes: Int, startHour: Int, endHour: Int)
    fun cancelReminder()
    fun isReminderScheduled(): Boolean
}

expect class WaterReminderSchedulerImpl : WaterReminderScheduler
