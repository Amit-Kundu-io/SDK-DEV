package com.amit_kundu_io.voicevault_sdk.domain.use_case

import android.util.Log
import com.amit_kundu_io.voicevault_sdk.core.NetworkResource
import com.amit_kundu_io.voicevault_sdk.data.repo.Repository
import kotlinx.coroutines.delay
import java.io.File


/**
 * Use case for uploading audio data (or metadata) to the server.
 */
internal class UploadAudioUseCase(
    private val repository: Repository
) {

    /**
     * Executes the use case.
     * @param prompt The prompt or metadata associated with the audio.
     * @return A [NetworkResource] representing the result of the operation.
     */
    suspend operator fun invoke(
        id: String,
        file: File
    ): NetworkResource<String> {
        Log.d("FILE_UPLOADING", "invoke: Start Upload")

        delay(5_000)
        Log.d("FILE_UPLOADING", "invoke: Start Upload Success")

        return NetworkResource.Success("Success")
    }
}