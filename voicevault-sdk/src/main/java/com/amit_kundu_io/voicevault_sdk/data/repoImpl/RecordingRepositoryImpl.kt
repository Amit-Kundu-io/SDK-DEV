/**
 * RecordingRepositoryImpl.kt
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

package com.amit_kundu_io.voicevault_sdk.data.repoImpl


import com.amit_kundu_io.voicevault_sdk.audio.AudioRecorder
import com.amit_kundu_io.voicevault_sdk.audio.AudioValidator
import com.amit_kundu_io.voicevault_sdk.data.repo.RecordingRepository
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Implementation of [RecordingRepository] that delegates to an [AudioRecorder]
 * and uses an [AudioValidator] to ensure recording quality.
 */
internal class
RecordingRepositoryImpl(

    private val recorder:
    AudioRecorder,

    private val validator:
    AudioValidator
) : RecordingRepository {

    override val isRecording: StateFlow<Boolean>
        get() = recorder.isRecordingFlow

    override val recordingTime: StateFlow<Long>
        get() = recorder.recordingTime

    override suspend fun
            startRecording() {

        recorder.startRecording()
    }

    override suspend fun
            pauseRecording() {

        recorder.pauseRecording()
    }

    override suspend fun
            resumeRecording() {

        recorder.resumeRecording()
    }

    override suspend fun stopRecording(): File {

        val file = recorder.stopRecording()

        validator.validate(file)
        return file
    }

    override suspend fun delete(
        file: File
    ) {

        if (
            file.exists()
        ) {

            file.delete()
        }
    }
}