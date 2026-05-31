package com.amit_kundu_io.voicevault_sdk.domain.use_case

import com.amit_kundu_io.voicevault_sdk.core.NetworkResource
import com.amit_kundu_io.voicevault_sdk.data.repo.Repository


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
        prompt: String
    ): NetworkResource<String> {

        return NetworkResource.Success("Success")
    }
}