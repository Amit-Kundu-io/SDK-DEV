/**
 * PlaybackState.kt
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

/**
 * Represents the various states of audio playback.
 */
sealed interface PlaybackState {

    /**
     * Player is idle and ready to load a file.
     */
    data object Idle : PlaybackState

    /**
     * Audio is currently playing.
     */
    data object Playing : PlaybackState

    /**
     * Audio playback is paused.
     */
    data object Paused : PlaybackState

    /**
     * Audio is being prepared for playback.
     */
    data object Preparing : PlaybackState

    /**
     * Audio playback has finished.
     */
    data object Completed : PlaybackState

    /**
     * An error occurred during playback.
     * @property throwable The cause of the error.
     */
    data class Error(
        val throwable: Throwable
    ) : PlaybackState
}