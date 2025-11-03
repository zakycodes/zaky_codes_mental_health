package com.zakycodes.zcmh.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import java.util.concurrent.TimeUnit

class AlarmScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val WORK_NAME = "meal_alarm_work"
        const val SNOOZE_REQUEST_CODE = 2001
    }

    fun scheduleAlarm(startTimeMillis: Long) {
        val currentTime = System.currentTimeMillis()
        val initialDelay = if (startTimeMillis > currentTime) {
            startTimeMillis - currentTime
        } else {
            0L
        }

        // Cancel existing alarms
        cancelAlarm()

        // Schedule dengan WorkManager untuk repeat
        val alarmRequest = PeriodicWorkRequestBuilder<MealAlarmWorker>(
            4, TimeUnit.HOURS  // ← PRODUCTION: 4 jam
            // 1, TimeUnit.MINUTES  // ← TESTING: 1 menit
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            alarmRequest
        )
    }

    fun scheduleSnoozeAlarm() {
        val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000) // 10 menit

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            SNOOZE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set exact alarm untuk snooze
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                snoozeTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                snoozeTime,
                pendingIntent
            )
        }
    }

    fun cancelAlarm() {
        workManager.cancelUniqueWork(WORK_NAME)

        // Cancel snooze alarm juga
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            SNOOZE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

// Worker untuk trigger alarm
class MealAlarmWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        // Trigger alarm receiver
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        applicationContext.sendBroadcast(intent)

        return Result.success()
    }
}