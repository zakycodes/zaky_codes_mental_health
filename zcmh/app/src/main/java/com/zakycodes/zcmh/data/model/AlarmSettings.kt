package com.zakycodes.zcmh.data.model


data class AlarmSettings(
    val isEnabled: Boolean = false,
    val startTime: Long = System.currentTimeMillis(),
    val intervalHours: Int = 4
)