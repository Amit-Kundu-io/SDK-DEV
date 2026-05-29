package com.amit_kundu_io.sdk.data


import com.amit_kundu_io.sdk.core.AiResult

internal class AiRepositoryImpl(
    private val api: AiApiService
) : AiRepository {

    override suspend fun generateText(
        prompt: String
    ): AiResult<String> {

        return try {

            val response =
                api.generateText(prompt)

            AiResult.Success(
                response.text
            )

        } catch (t: Throwable) {

            AiResult.Error(t)
        }
    }
}