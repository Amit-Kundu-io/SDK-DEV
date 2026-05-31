/**
 * RecordingRepository.kt
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

import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Repository interface for managing audio recording.
 */
interface RecordingRepository {

    /**
     * A [StateFlow] indicating whether a recording is currently in progress.
     */
    val isRecording: StateFlow<Boolean>

    /**
     * A [StateFlow] representing the current recording duration in milliseconds.
     */
    val recordingTime: StateFlow<Long>

    /**
     * Starts a new recording session.
     */
    suspend fun startRecording()

    /**
     * Pauses the current recording session.
     */
    suspend fun pauseRecording()

    /**
     * Resumes a paused recording session.
     */
    suspend fun resumeRecording()

    /**
     * Stops the current recording session and returns the recorded [File].
     */
    suspend fun stopRecording(): File

    /**
     * Deletes the specified recorded [file].
     */
    suspend fun delete(file: File)
}