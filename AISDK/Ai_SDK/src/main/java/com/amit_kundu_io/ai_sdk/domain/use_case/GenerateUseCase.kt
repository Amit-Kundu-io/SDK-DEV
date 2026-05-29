package com.amit_kundu_io.ai_sdk.domain.use_case

import com.amit_kundu_io.ai_sdk.data.AiRepository
import com.amit_kundu_io.ai_sdk.data.AiResult


/**
 * Use case for text generation.
 * Encapsulates business logic and error handling.
 */
class GenerateUseCase(private val repo: AiRepository) {
    suspend operator fun invoke(prompt: String): AiResult<String> {
        return repo.generateText(prompt)
    }
}
