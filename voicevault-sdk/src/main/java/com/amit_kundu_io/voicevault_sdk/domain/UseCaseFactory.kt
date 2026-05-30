package com.amit_kundu_io.voicevault_sdk.domain



import com.amit_kundu_io.voicevault_sdk.data.repo.Repository
import com.amit_kundu_io.voicevault_sdk.domain.use_case.UploadAudioUseCase

internal class UseCaseFactory(
    repository: Repository
) {

    val uploadAudioUseCase by lazy {
        UploadAudioUseCase(repository)
    }

}