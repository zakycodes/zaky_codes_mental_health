package com.zakycodes.zcmh.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MusicReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MusicReceiver", "Received action: ${intent.action}")

        val action = when (intent.action) {
            "com.zakycodes.zcmh.ACTION_PLAY" -> "PLAY"
            "com.zakycodes.zcmh.ACTION_PAUSE" -> "PAUSE"
            "com.zakycodes.zcmh.ACTION_NEXT" -> "NEXT"
            "com.zakycodes.zcmh.ACTION_PREVIOUS" -> "PREVIOUS"
            "com.zakycodes.zcmh.ACTION_STOP" -> "STOP"
            else -> return
        }

        Log.d("MusicReceiver", "Handling action: $action")
        MusicControlHandler.handleAction(action)
    }
}