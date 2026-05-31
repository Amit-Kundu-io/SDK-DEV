package com.amit_kundu_io.voicevault_sdk.data.repoImpl

import com.amit_kundu_io.voicevault_sdk.data.repo.Repository
import com.amit_kundu_io.voicevault_sdk.data.service.ApiService


/**
 * Implementation of the [Repository] interface.
 * @property api The API service for network operations.
 */
internal class RepositoryImpl(
    private val api: ApiService
) : Repository {

}