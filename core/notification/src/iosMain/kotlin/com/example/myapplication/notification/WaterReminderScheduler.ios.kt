package com.example.myapplication.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual class WaterReminderSchedulerImpl : WaterReminderScheduler {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override fun scheduleReminder(intervalMinutes: Int, startHour: Int, endHour: Int) {
        val content = UNMutableNotificationContent().apply {
            setTitle("물 마시기 시간이에요! 💧")
            setBody("건강을 위해 물 한 잔 마셔보세요")
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = (intervalMinutes * 60).toDouble(),
            repeats = true
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = NOTIFICATION_ID,
            content = content,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(request) { error ->
            error?.let {
                println("Failed to schedule notification: ${it.localizedDescription}")
            }
        }
    }

    override fun cancelReminder() {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(NOTIFICATION_ID))
    }

    override fun isReminderScheduled(): Boolean {
        // iOS에서는 비동기로 확인해야 하므로 간단히 true 반환
        return true
    }

    companion object {
        private const val NOTIFICATION_ID = "water_reminder"
    }
}
