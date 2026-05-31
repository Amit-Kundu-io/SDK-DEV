/**
 * PlaybackRepository.kt
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

package com.amit_kundu_io.voicevault_sdk.data.repo

import java.io.File


/**
 * Repository interface for managing audio playback.
 */
interface PlaybackRepository {

    /**
     * Starts playback of the audio file at the specified [path].
     */
    suspend fun play(path:String)

    /**
     * Starts playback of the audio file at the specified [File].
     */
    suspend fun play(file: File)

    /**
     * Pauses the current playback.
     */
    suspend fun pause()

    /**
     * Resumes the paused playback.
     */
    suspend fun resume()

    /**
     * Stops the playback.
     */
    suspend fun stop()
}