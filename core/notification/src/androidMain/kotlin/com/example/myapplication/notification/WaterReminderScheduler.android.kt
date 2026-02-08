package com.example.myapplication.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Calendar

actual class WaterReminderSchedulerImpl(
    private val context: Context
) : WaterReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "물 마시기 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "물 마시기 알림을 받습니다"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun scheduleReminder(intervalMinutes: Int, startHour: Int, endHour: Int) {
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            putExtra(EXTRA_INTERVAL, intervalMinutes)
            putExtra(EXTRA_START_HOUR, startHour)
            putExtra(EXTRA_END_HOUR, endHour)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, intervalMinutes)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intervalMinutes * 60 * 1000L,
            pendingIntent
        )
    }

    override fun cancelReminder() {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun isReminderScheduled(): Boolean {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
    }

    companion object {
        const val CHANNEL_ID = "water_reminder_channel"
        const val REQUEST_CODE = 1001
        const val EXTRA_INTERVAL = "interval"
        const val EXTRA_START_HOUR = "start_hour"
        const val EXTRA_END_HOUR = "end_hour"
    }
}

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val startHour = intent.getIntExtra(WaterReminderSchedulerImpl.EXTRA_START_HOUR, 8)
        val endHour = intent.getIntExtra(WaterReminderSchedulerImpl.EXTRA_END_HOUR, 22)

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // 설정된 시간 범위 내에만 알림 표시
        if (currentHour in startHour until endHour) {
            showNotification(context)
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, WaterReminderSchedulerImpl.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("물 마시기 시간이에요! 💧")
            .setContentText("건강을 위해 물 한 잔 마셔보세요")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
