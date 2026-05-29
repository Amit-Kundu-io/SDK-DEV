package com.amit_kundu_io.sdk.data


import com.amit_kundu_io.sdk.core.safeApiCall
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class AiApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    suspend fun generateText(
        prompt: String
    ): GenerateResponseDto {

        return safeApiCall {
            GenerateResponseDto("Amit Kundu")

/*
            client.post(
                "$baseUrl/v1/chat/completions"
            ) {

                setBody(

                    GenerateRequestDto(
                        model = "gpt-demo",
                        prompt = prompt
                    )
                )
            }.body()
            */
        }
    }
}