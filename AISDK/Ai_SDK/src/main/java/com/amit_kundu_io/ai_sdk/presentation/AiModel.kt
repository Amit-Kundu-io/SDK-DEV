package com.amit_kundu_io.ai_sdk.presentation

import com.amit_kundu_io.ai_sdk.data.AiResult
import com.amit_kundu_io.ai_sdk.domain.use_case.GenerateUseCase



/**
 * Public-facing AI model API.
 * Wraps domain use cases and returns [AiResult].
 */
class AiModel(private val generateUseCase: GenerateUseCase) {
    suspend fun generateText(prompt: String): AiResult<String> {
        return generateUseCase(prompt)
    }
}
