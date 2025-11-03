package com.zakycodes.zcmh.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zakycodes.zcmh.R
import com.zakycodes.zcmh.data.model.MusicCategory
import com.zakycodes.zcmh.data.model.MusicTrack
import com.zakycodes.zcmh.utils.MusicPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val musicPlayer = MusicPlayer(application)

    val isPlaying = musicPlayer.isPlaying
    val isLooping = musicPlayer.isLooping

    private val _currentTrackIndex = MutableStateFlow(0) // DEFAULT ke 0 (Bug Fix #1)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    private val _selectedTimer = MutableStateFlow<Int?>(null)
    val selectedTimer: StateFlow<Int?> = _selectedTimer

    // Filter kategori
    private val _selectedCategory = MutableStateFlow<MusicCategory?>(null)
    val selectedCategory: StateFlow<MusicCategory?> = _selectedCategory

    private var timerJob: Job? = null
    private var positionJob: Job? = null

    // Semua musik (tanpa filter)
    private val allMusicTracks = listOf(
        // Kategori RELAX
        MusicTrack(1, R.raw.relax_music_1, "Deep Relaxation", "3 Hz Delta - Relaksasi mendalam", MusicCategory.RELAX),
        MusicTrack(2, R.raw.relax_music_2, "Anxiety Relief", "6 Hz Theta - Mengurangi kecemasan", MusicCategory.RELAX),
        MusicTrack(3, R.raw.relax_music_3, "Calm Mind", "7 Hz Theta - Menenangkan pikiran", MusicCategory.RELAX),

        // Kategori SLEEP
        MusicTrack(4, R.raw.sleep_music_1, "Night Sleep", "2 Hz Delta - Tidur nyenyak", MusicCategory.SLEEP),
        MusicTrack(5, R.raw.sleep_music_2, "Deep Sleep", "2.5 Hz Delta - Tidur dalam", MusicCategory.SLEEP),
        MusicTrack(6, R.raw.sleep_music_3, "Bedtime Relaxation", "3 Hz Delta - Relaksasi sebelum tidur", MusicCategory.SLEEP),

        // Kategori FOCUS
        MusicTrack(7, R.raw.focus_music_1, "Work Focus", "40 Hz Gamma - Fokus kerja", MusicCategory.FOCUS),
        MusicTrack(8, R.raw.focus_music_2, "Study Boost", "40 Hz Gamma - Konsentrasi belajar", MusicCategory.FOCUS),
//        MusicTrack(9, R.raw.focus_music_3, "Productivity", "40 Hz Gamma - Produktivitas maksimal", MusicCategory.FOCUS)
    )

    // Musik yang ditampilkan (berdasarkan filter)
    private val _musicTracks = MutableStateFlow(allMusicTracks)
    val musicTracks: StateFlow<List<MusicTrack>> = _musicTracks

    init {
        startPositionUpdater()

        // Set completion listener untuk auto-play next track (Bug Fix #3)
        musicPlayer.setOnCompletionListener {
            if (!isLooping.value) {
                nextTrack()
            }
        }

        // Load default track pertama (Bug Fix #1)
        loadDefaultTrack()
    }

    // Load track default tanpa auto-play
    private fun loadDefaultTrack() {
        val tracks = _musicTracks.value
        if (tracks.isNotEmpty()) {
            musicPlayer.prepare(tracks[0].resourceId)
            _duration.value = musicPlayer.getDuration()
        }
    }

    fun playTrack(index: Int) {
        _currentTrackIndex.value = index
        val tracks = _musicTracks.value
        if (index < tracks.size) {
            musicPlayer.play(tracks[index].resourceId)
            _duration.value = musicPlayer.getDuration()
            startPositionUpdater()
        }
    }

    fun togglePlayPause() {
        if (isPlaying.value) {
            musicPlayer.pause()
            stopPositionUpdater()
        } else {
            if (_currentTrackIndex.value < _musicTracks.value.size) {
                musicPlayer.resume()
                startPositionUpdater()
            } else {
                playTrack(0)
            }
        }
    }

    fun stopMusic() {
        musicPlayer.stop()
        stopPositionUpdater()
        cancelTimer()
    }

    fun nextTrack() {
        val tracks = _musicTracks.value
        if (tracks.isEmpty()) return

        val nextIndex = (_currentTrackIndex.value + 1) % tracks.size
        playTrack(nextIndex)
    }

    fun previousTrack() {
        val tracks = _musicTracks.value
        if (tracks.isEmpty()) return

        val prevIndex = if (_currentTrackIndex.value > 0) {
            _currentTrackIndex.value - 1
        } else {
            tracks.size - 1
        }
        playTrack(prevIndex)
    }

    fun toggleLoop() {
        musicPlayer.toggleLoop()
    }

    fun seekTo(position: Int) {
        musicPlayer.seekTo(position)
        _currentPosition.value = position
    }

    fun setTimer(minutes: Int) {
        cancelTimer()
        _selectedTimer.value = minutes

        timerJob = viewModelScope.launch {
            delay(minutes * 60 * 1000L)
            stopMusic()
            _selectedTimer.value = null
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _selectedTimer.value = null
    }

    // Filter musik berdasarkan kategori
    fun filterByCategory(category: MusicCategory?) {
        _selectedCategory.value = category
        _musicTracks.value = if (category == null) {
            allMusicTracks
        } else {
            allMusicTracks.filter { it.category == category }
        }

        // Reset ke track pertama dan load (tapi jangan auto-play)
        _currentTrackIndex.value = 0
        if (!isPlaying.value && _musicTracks.value.isNotEmpty()) {
            musicPlayer.prepare(_musicTracks.value[0].resourceId)
            _duration.value = musicPlayer.getDuration()
        }
    }

    private fun startPositionUpdater() {
        stopPositionUpdater()
        positionJob = viewModelScope.launch {
            while (isPlaying.value) {
                _currentPosition.value = musicPlayer.getCurrentPosition()
                _duration.value = musicPlayer.getDuration()
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdater() {
        positionJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.release()
        stopPositionUpdater()
        cancelTimer()
    }

    fun formatTime(millis: Int): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}