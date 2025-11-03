package com.zakycodes.zcmh.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zakycodes.zcmh.MainActivity
import com.zakycodes.zcmh.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentResourceId: Int? = null

    // Media Session untuk notification controls (Bug Fix #2)
    private var mediaSession: MediaSessionCompat? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "music_player_channel"
        private const val CHANNEL_NAME = "Music Player"

        // ✅ PERBAIKAN: Hapus prefix package
        const val ACTION_PLAY = "com.zakycodes.zcmh.ACTION_PLAY"
        const val ACTION_PAUSE = "com.zakycodes.zcmh.ACTION_PAUSE"
        const val ACTION_NEXT = "com.zakycodes.zcmh.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.zakycodes.zcmh.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.zakycodes.zcmh.ACTION_STOP"
    }

    init {
        createNotificationChannel()
        initMediaSession()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music Player Controls"
                setShowBadge(false)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(context, "MusicPlayerSession").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    resume()
                }

                override fun onPause() {
                    pause()
                }

                override fun onSkipToNext() {
                    // Handled by notification intent
                }

                override fun onSkipToPrevious() {
                    // Handled by notification intent
                }

                override fun onStop() {
                    stop()
                }
            })

            isActive = true
        }
    }

    // Prepare track tanpa auto-play (untuk default load)
    fun prepare(resourceId: Int) {
        if (currentResourceId == resourceId && mediaPlayer != null) {
            return
        }

        release()
        currentResourceId = resourceId

        try {
            mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                isLooping = _isLooping.value
                setOnPreparedListener {
                    // Prepared, tapi tidak auto-play
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(resourceId: Int) {
        if (currentResourceId == resourceId && mediaPlayer != null) {
            if (!_isPlaying.value) {
                resume()
            }
            return
        }

        release()
        currentResourceId = resourceId

        try {
            mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                isLooping = _isLooping.value
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                    showNotification()
                    updateMediaSessionState(PlaybackStateCompat.STATE_PLAYING)
                }
            }

            mediaPlayer?.start()
            _isPlaying.value = true
            showNotification()
            updateMediaSessionState(PlaybackStateCompat.STATE_PLAYING)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
        showNotification()
        updateMediaSessionState(PlaybackStateCompat.STATE_PAUSED)
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
        showNotification()
        updateMediaSessionState(PlaybackStateCompat.STATE_PLAYING)
    }

    fun stop() {
        mediaPlayer?.stop()
        _isPlaying.value = false
        hideNotification()
        updateMediaSessionState(PlaybackStateCompat.STATE_STOPPED)
        release()
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun toggleLoop() {
        _isLooping.value = !_isLooping.value
        mediaPlayer?.isLooping = _isLooping.value
    }

    // Set completion listener untuk auto-next (Bug Fix #3)
    fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer?.setOnCompletionListener {
            if (!_isLooping.value) {
                listener()
            }
        }
    }

    private fun updateMediaSessionState(state: Int) {
        val position = getCurrentPosition().toLong()
        val playbackState = PlaybackStateCompat.Builder()
            .setState(state, position, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_STOP
            )
            .build()

        mediaSession?.setPlaybackState(playbackState)
    }

    // Show Media Notification (Bug Fix #2)
    private fun showNotification() {
        val playPauseIcon = if (_isPlaying.value) {
            R.drawable.ic_pause // Akan kita buat
        } else {
            R.drawable.ic_play // Akan kita buat
        }

        val playPauseAction = if (_isPlaying.value) ACTION_PAUSE else ACTION_PLAY
        val playPauseTitle = if (_isPlaying.value) "Pause" else "Play"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note) // Akan kita buat
            .setContentTitle("Musik Anxiety Relief")
            .setContentText("Playing music therapy")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(_isPlaying.value)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                R.drawable.ic_previous,
                "Previous",
                createPendingIntent(ACTION_PREVIOUS)
            )
            .addAction(
                playPauseIcon,
                playPauseTitle,
                createPendingIntent(playPauseAction)
            )
            .addAction(
                R.drawable.ic_next,
                "Next",
                createPendingIntent(ACTION_NEXT)
            )
            .setContentIntent(createContentIntent())
            .setDeleteIntent(createPendingIntent(ACTION_STOP))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun hideNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action).apply {
            setPackage(context.packageName) // ✅ TAMBAHKAN INI KEMBALI
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(context, action.hashCode(), intent, flags)
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentResourceId = null
    }

    fun cleanup() {
        release()
        hideNotification()
        mediaSession?.release()
        mediaSession = null
    }
}