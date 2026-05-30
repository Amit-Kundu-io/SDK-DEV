package com.amit_kundu_io.sdk.presentation


import com.amit_kundu_io.sdk.core.AiResult
import com.amit_kundu_io.sdk.core.Validation

class AiModule internal constructor(
    private val container: AiAppContainer
) {

    suspend fun generate(
        prompt: String
    ): AiResult<String> {

        Validation.validatePrompt(prompt)

        return container
            .useCases
            .generate(
                prompt
            )
    }
}