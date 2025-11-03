package com.zakycodes.zcmh.utils

import com.zakycodes.zcmh.ui.viewmodel.MusicViewModel

object MusicControlHandler {
    private var viewModel: MusicViewModel? = null

    fun setViewModel(vm: MusicViewModel) {
        viewModel = vm
    }

    fun handleAction(action: String) {
        when (action) {
            "PLAY", "PAUSE" -> viewModel?.togglePlayPause()
            "NEXT" -> viewModel?.nextTrack()
            "PREVIOUS" -> viewModel?.previousTrack()
            "STOP" -> viewModel?.stopMusic()
        }
    }

    fun clearViewModel() {
        viewModel = null
    }
}