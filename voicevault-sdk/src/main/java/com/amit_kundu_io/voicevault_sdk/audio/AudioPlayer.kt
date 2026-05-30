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

interface AudioPlayer {

    val playbackState: StateFlow<PlaybackState>
    val playbackTime: StateFlow<PlaybackTime>

    suspend fun play(path: String)

    suspend fun pause()

    suspend fun resume()

    suspend fun stop()

    fun isPlaying(): Boolean
}