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

interface RecordingRepository {

    val isRecording: StateFlow<Boolean>

    val recordingTime: StateFlow<Long>

    suspend fun startRecording()

    suspend fun pauseRecording()

    suspend fun resumeRecording()

    suspend fun stopRecording(): File

    suspend fun delete(file: File)
}