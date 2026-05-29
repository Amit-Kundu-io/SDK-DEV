package com.amit_kundu_io.ai_sdk.presentation

import android.content.Context
import com.amit_kundu_io.ai_sdk.core.AiAppContainer
import com.amit_kundu_io.ai_sdk.core.MyAiNotInitializedException


/**
 * Entry point for the MyAi SDK.
 * Handles initialization, shutdown, and exposes the AI model.
 */
object MyAi {
    @Volatile private var initialized = false
    private val lock = Any()

    /**
     * Initializes the SDK with application context and API key.
     * Must be called before using [model].
     */
    fun initialize(apiKey: String) {
        synchronized(lock) {
            if (initialized) return
            AiAppContainer.initialize(apiKey)
            initialized = true
        }
    }

    /**
     * Returns the AI model interface.
     * Throws [MyAiNotInitializedException] if SDK not initialized.
     */
    fun model(): AiModel {
        return AiAppContainer.model()
    }


    /**
     * Shuts down SDK resources.
     */
    fun shutdown() {
        AiAppContainer.shutdown()
        initialized = false
    }
}
