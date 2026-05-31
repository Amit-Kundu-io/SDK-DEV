/**
 * AudioPlayer.kt
 *
 * Author      : Amit Kundu
 * Created On  : 31/05/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */

package com.amit_kundu_io.voicevault_sdk.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining the contract for audio playback operations.
 */
interface AudioPlayer {

    /**
     * A [StateFlow] representing the current [PlaybackState].
     */
    val playbackState: StateFlow<PlaybackState>

    /**
     * A [StateFlow] representing the current [PlaybackTime].
     */
    val playbackTime: StateFlow<PlaybackTime>

    /**
     * Starts playing the audio file at the specified path.
     * @param path The absolute path to the audio file.
     */
    suspend fun play(path: String)

    /**
     * Pauses the current audio playback.
     */
    suspend fun pause()

    /**
     * Resumes the paused audio playback.
     */
    suspend fun resume()

    /**
     * Stops the audio playback.
     */
    suspend fun stop()

    /**
     * Returns whether the player is currently playing.
     * @return True if playing, false otherwise.
     */
    fun isPlaying(): Boolean
}