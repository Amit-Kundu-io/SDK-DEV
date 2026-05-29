package com.amit_kundu_io.ai_sdk.data



/**
 * Repository abstraction for AI operations.
 */
interface AiRepository {
    suspend fun generateText(prompt: String): AiResult<String>
}
