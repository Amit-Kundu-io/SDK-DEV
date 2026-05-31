package com.amit_kundu_io.voicevault_sdk.domain



import android.content.Context
import com.amit_kundu_io.voicevault_sdk.data.repo.Repository
import com.amit_kundu_io.voicevault_sdk.domain.use_case.StartWorkerUseCase
import com.amit_kundu_io.voicevault_sdk.domain.use_case.UploadAudioUseCase

/**
 * Factory class for creating domain layer use cases.
 * @property repository The main repository used by use cases.
 */
internal class UseCaseFactory(
    repository: Repository,
    context: Context
) {

    /**
     * Use case for uploading audio files.
     */
    val uploadAudioUseCase by lazy { UploadAudioUseCase(repository) }
    val startWorkerUseCase by lazy { StartWorkerUseCase(context) }

}