package com.amit_kundu_io.voicevault_sdk.data.service

import io.ktor.client.HttpClient

/**
 * ApiService.kt
 *
 * Author      : Amit Kundu
 * Created On  : 30/05/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */
/**
 * Service class for making API requests using [HttpClient].
 * @property client The Ktor HTTP client.
 * @property baseUrl The base URL for the API.
 */
internal class ApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {

}