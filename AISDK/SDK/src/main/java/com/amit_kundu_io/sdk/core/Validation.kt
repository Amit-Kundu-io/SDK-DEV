package com.amit_kundu_io.sdk.core



internal object Validation {

    fun validateConfig(
        config: AiConfig
    ) {

        require(
            config.apiKey.isNotBlank()
        ) {
            "apiKey cannot be empty."
        }

        require(
            config.baseUrl.isNotBlank()
        ) {
            "baseUrl cannot be empty."
        }
    }

    fun validatePrompt(
        prompt: String
    ) {

        require(
            prompt.isNotBlank()
        ) {
            "prompt cannot be empty."
        }
    }
}