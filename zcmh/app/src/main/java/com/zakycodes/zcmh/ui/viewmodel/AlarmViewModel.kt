package com.zakycodes.zcmh.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zakycodes.zcmh.data.model.AlarmSettings
import com.zakycodes.zcmh.utils.AlarmScheduler
import com.zakycodes.zcmh.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler(application)

    private val _alarmSettings = MutableStateFlow(AlarmSettings())
    val alarmSettings: StateFlow<AlarmSettings> = _alarmSettings

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog

    private val _nextAlarmTime = MutableStateFlow("")
    val nextAlarmTime: StateFlow<String> = _nextAlarmTime

    private val _upcomingAlarms = MutableStateFlow<List<String>>(emptyList())
    val upcomingAlarms: StateFlow<List<String>> = _upcomingAlarms

    init {
        NotificationHelper.createNotificationChannel(application)
    }

    // Quick Start - Mulai dari sekarang
    fun startQuickAlarm() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val calendar = Calendar.getInstance()

            // Alarm pertama 4 jam dari sekarang
            calendar.add(Calendar.HOUR_OF_DAY, 4)
            val firstAlarmTime = calendar.timeInMillis

            _alarmSettings.value = AlarmSettings(
                isEnabled = true,
                startTime = firstAlarmTime,
                intervalHours = 4
            )

            alarmScheduler.scheduleAlarm(firstAlarmTime)

            // Calculate upcoming alarms
            calculateUpcomingAlarms(firstAlarmTime)

            _showConfirmationDialog.value = true
        }
    }

    fun toggleAlarm() {
        if (_alarmSettings.value.isEnabled) {
            disableAlarm()
        } else {
            // Quick start
            startQuickAlarm()
        }
    }

    private fun disableAlarm() {
        viewModelScope.launch {
            _alarmSettings.value = AlarmSettings(isEnabled = false)
            alarmScheduler.cancelAlarm()
            _upcomingAlarms.value = emptyList()
            _nextAlarmTime.value = ""
        }
    }

    private fun calculateUpcomingAlarms(startTime: Long) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val alarmTimes = mutableListOf<String>()

        var nextTime = startTime
        for (i in 0 until 5) { // Next 5 alarms
            alarmTimes.add(timeFormat.format(Date(nextTime)))
            nextTime += 4 * 60 * 60 * 1000 // Add 4 hours
        }

        _nextAlarmTime.value = alarmTimes.first()
        _upcomingAlarms.value = alarmTimes
    }

    fun dismissConfirmationDialog() {
        _showConfirmationDialog.value = false
    }
}