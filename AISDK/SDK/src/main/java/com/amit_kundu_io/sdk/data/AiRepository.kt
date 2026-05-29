package com.amit_kundu_io.sdk.data

import com.amit_kundu_io.sdk.core.AiResult


interface AiRepository {

    suspend fun generateText(
        prompt: String
    ): AiResult<String>
}