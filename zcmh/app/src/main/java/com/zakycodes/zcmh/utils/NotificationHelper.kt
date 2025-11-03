package com.zakycodes.zcmh.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zakycodes.zcmh.R

object NotificationHelper {

    private const val CHANNEL_ID = "meal_alarm_channel"
    private const val CHANNEL_NAME = "Meal Reminders"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Notifikasi pengingat makan"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMealNotificationWithActions(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Dismiss Action
        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_DISMISS
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Action
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_SNOOZE
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("⏰ Waktunya Makan!")
            .setContentText("Sudah 4 jam sejak makan terakhir. Yuk makan untuk jaga kesehatan mental!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Sudah 4 jam sejak makan terakhir. Yuk makan untuk jaga kesehatan mental!\n\nMakan teratur membantu mengurangi anxiety dan menjaga mood tetap stabil."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(false)
            .addAction(R.mipmap.ic_launcher_foreground, "Sudah Makan", dismissPendingIntent)
            .addAction(R.mipmap.ic_launcher_foreground, "Tunda 10 Menit", snoozePendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}