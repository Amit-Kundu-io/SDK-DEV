/**
 * AudioRecorder.kt
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
import java.io.File


/**
 * Interface defining the contract for audio recording operations.
 */
interface AudioRecorder {

    /**
     * A [StateFlow] that emits true when recording is in progress, false otherwise.
     */
    val isRecordingFlow: StateFlow<Boolean>

    /**
     * A [StateFlow] that emits the current recording duration in milliseconds.
     */
    val recordingTime: StateFlow<Long>

    /**
     * Starts a new audio recording session.
     */
    suspend fun startRecording()

    /**
     * Pauses the current audio recording session.
     */
    suspend fun pauseRecording()

    /**
     * Resumes a paused audio recording session.
     */
    suspend fun resumeRecording()

    /**
     * Stops the current audio recording session and returns the recorded [File].
     * @return The recorded audio file.
     */
    suspend fun stopRecording(): File

    /**
     * Returns whether the recorder is currently recording.
     * @return True if recording, false otherwise.
     */
    fun isRecording(): Boolean
}