package com.amit_kundu_io.voicevault_sdk.audio

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/**
 * AudioPlayerImpl.kt
 *
 * Enterprise-grade audio playback implementation.
 *
 * Features:
 * - Deterministic state machine (Idle → Preparing → Playing → Paused → Completed/Error).
 * - Thread-safe operations using Mutex.
 * - Race-safe MediaPlayer callbacks (validated instance).
 * - Lifecycle-safe coroutine scope injected externally.
 * - Robust error handling via Flow-only (no mixed exception channels).
 * - Playback time updates (current vs total).
 *
 * Author: Amit Kundu
 * Created On: 31/05/2026
 */
internal class AudioPlayerImpl(
    private val externalScope: CoroutineScope,              // injected scope for lifecycle
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate // injected dispatcher
) : AudioPlayer {

    private val mutex = Mutex()
    private var player: MediaPlayer? = null
    private var positionJob: Job? = null

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> get() = _playbackState

    private val _playbackTime = MutableStateFlow(PlaybackTime(0L, 0L))
    override val playbackTime: StateFlow<PlaybackTime> get() = _playbackTime

    /**
     * Begin playback of a local file path.
     * Validates state and file before preparing MediaPlayer.
     */
    override suspend fun play(path: String) {
        mutex.withLock {
            if (_playbackState.value == PlaybackState.Preparing ||
                _playbackState.value == PlaybackState.Playing
            ) return // prevent duplicate play calls

            releasePlayer(resetState = false)
            _playbackState.value = PlaybackState.Preparing
        }

        try {
            // Heavy I/O offloaded to background dispatcher
            withContext(Dispatchers.IO) {
                val file = File(path)
                require(file.exists() && file.canRead()) {
                    "Invalid audio path: $path"
                }
                player = MediaPlayer().apply { setDataSource(path) }
            }

            player?.apply {
                setOnPreparedListener { mp ->
                    if (player !== mp) return@setOnPreparedListener
                    externalScope.launch(dispatcher) {
                        mutex.withLock {
                            runCatching { mp.start() }
                                .onSuccess { _playbackState.value = PlaybackState.Playing }
                                .onFailure { _playbackState.value = PlaybackState.Error(it) }
                        }
                        startPositionUpdates() //  outside lock
                    }
                }

                setOnCompletionListener { mp ->
                    externalScope.launch(dispatcher) {
                        mutex.withLock {
                            if (player === mp) cleanupAfterCompletion()
                        }
                    }
                }

                setOnErrorListener { mp, what, extra ->
                    externalScope.launch(dispatcher) {
                        mutex.withLock {
                            if (player === mp) {
                                _playbackState.value = PlaybackState.Error(
                                    RuntimeException("Playback failed: $what $extra")
                                )
                                releasePlayer()
                            }
                        }
                    }
                    true
                }

                prepareAsync()
            }
        } catch (t: Throwable) {
            releasePlayer()
            _playbackState.value = PlaybackState.Error(t) //  Flow-only error reporting
        }
    }

    /**
     * Pause playback if currently playing.
     */
    override suspend fun pause() {
        mutex.withLock {
            if (_playbackState.value == PlaybackState.Playing) {
                player?.pause()
                _playbackState.value = PlaybackState.Paused
                stopPositionUpdates()
            }
        }
    }

    /**
     * Resume playback if currently paused.
     */
    override suspend fun resume() {
        mutex.withLock {
            if (_playbackState.value == PlaybackState.Paused) {
                player?.start()
                _playbackState.value = PlaybackState.Playing
                startPositionUpdates()
            }
        }
    }

    /**
     * Stop playback and release resources.
     */
    override suspend fun stop() {
        mutex.withLock { releasePlayer() }
    }

    /**
     * Safe check if player is currently playing.
     */
    override fun isPlaying(): Boolean =
        runCatching { player?.isPlaying ?: false }.getOrDefault(false)

    /**
     * Release MediaPlayer resources.
     */
    private fun releasePlayer(resetState: Boolean = true) {
        runCatching {
            player?.apply {
                try {
                    if (isPlaying) stop()
                } catch (_: Throwable) { }
                reset()
                release()
            }
        }.onFailure {
            Log.e("VoiceVaultPlayer", "Release failed.", it)
        }

        player = null
        stopPositionUpdates()

        if (resetState) {
            _playbackState.value = PlaybackState.Idle
            _playbackTime.value = PlaybackTime(0L, 0L)
        }
    }

    /**
     * Cleanup after playback completion.
     */
    private fun cleanupAfterCompletion() {
        stopPositionUpdates()
        player?.release()
        player = null
        _playbackTime.value = PlaybackTime(0L, 0L)
        _playbackState.value = PlaybackState.Completed
    }

    /**
     * Start updating playback time every second.
     */
    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionJob = externalScope.launch(dispatcher) {
            while (isActive) {
                val mp = player ?: break
                val current = runCatching { mp.currentPosition.toLong() }.getOrDefault(0L)
                val total = runCatching { mp.duration.toLong() }.getOrDefault(0L)
                _playbackTime.value = PlaybackTime(total, current)
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }
}
