package com.amit_kundu_io.ai_sdk.data


import com.amit_kundu_io.ai_sdk.core.AiConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay

/**
 * Ktor-based implementation of [AiRepository].
 * Handles network calls and wraps results in [AiResult].
 */
class AiRepositoryImpl(
    private val client: HttpClient,
    private val config: AiConfig
) : AiRepository {

    override suspend fun generateText(prompt: String): AiResult<String> {
        return try {
            delay(2_000)
//            val response: String = client.post("${config.baseUrl}/text") {
//                header("Authorization", "Bearer ${config.apiKey}")
//                setBody(mapOf("prompt" to prompt))
//            }.body()
            AiResult.Success("Success")
        } catch (e: Exception) {
            AiResult.Error(e)
        }
    }
}
