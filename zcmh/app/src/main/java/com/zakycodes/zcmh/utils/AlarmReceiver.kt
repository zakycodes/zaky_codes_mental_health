package com.zakycodes.zcmh.utils

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISMISS = "com.zakycodes.zcmh.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.zakycodes.zcmh.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DISMISS -> {
                // User dismiss alarm
                AlarmSoundManager.stopAlarm(context)
            }
            ACTION_SNOOZE -> {
                // User snooze 10 menit
                AlarmSoundManager.stopAlarm(context)
                val alarmScheduler = AlarmScheduler(context)
                alarmScheduler.scheduleSnoozeAlarm()
            }
            else -> {
                // Alarm berbunyi
                playAlarmSound(context)
                NotificationHelper.showMealNotificationWithActions(context)
            }
        }
    }

    private fun playAlarmSound(context: Context) {
        // Wake up screen
        wakeUpScreen(context)

        // Play alarm sound
        AlarmSoundManager.playAlarm(context)

        // Vibrate
        vibratePhone(context)
    }

    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "ZCMH:AlarmWakeLock"
        )
        wakeLock.acquire(10000) // 10 seconds

        // Release wake lock after delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }, 10000)
    }

    private fun vibratePhone(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}

// Singleton untuk manage alarm sound
object AlarmSoundManager {
    private var ringtone: android.media.Ringtone? = null

    fun playAlarm(context: Context) {
        stopAlarm(context) // Stop previous alarm if any

        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            ringtone = RingtoneManager.getRingtone(context, alarmUri)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = false
            }

            ringtone?.play()

            // Auto stop after 60 seconds
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                stopAlarm(context)
            }, 60000)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAlarm(context: Context) {
        try {
            ringtone?.stop()
            ringtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}