package com.amit_kundu_io.voicevault_sdk.domain



import com.amit_kundu_io.voicevault_sdk.data.repo.Repository
import com.amit_kundu_io.voicevault_sdk.domain.use_case.UploadAudioUseCase

/**
 * Factory class for creating domain layer use cases.
 * @property repository The main repository used by use cases.
 */
internal class UseCaseFactory(
    repository: Repository
) {

    /**
     * Use case for uploading audio files.
     */
    val uploadAudioUseCase by lazy {
        UploadAudioUseCase(repository)
    }

}