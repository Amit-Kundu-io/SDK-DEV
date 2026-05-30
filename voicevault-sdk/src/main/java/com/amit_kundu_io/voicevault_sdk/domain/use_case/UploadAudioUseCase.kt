package com.amit_kundu_io.voicevault_sdk.domain.use_case

import com.amit_kundu_io.voicevault_sdk.core.NetworkResource
import com.amit_kundu_io.voicevault_sdk.data.repo.Repository


internal class UploadAudioUseCase(
    private val repository: Repository
) {

    suspend operator fun invoke(
        prompt: String
    ): NetworkResource<String> {

        return NetworkResource.Success("Success")
    }
}