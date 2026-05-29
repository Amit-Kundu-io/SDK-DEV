package com.amit_kundu_io.sdk.domain

import com.amit_kundu_io.sdk.core.AiResult
import com.amit_kundu_io.sdk.data.AiRepository


internal class GenerateUseCase(
    private val repository: AiRepository
) {

    suspend operator fun invoke(
        prompt: String
    ): AiResult<String> {

        return repository.generateText(
            prompt
        )
    }
}