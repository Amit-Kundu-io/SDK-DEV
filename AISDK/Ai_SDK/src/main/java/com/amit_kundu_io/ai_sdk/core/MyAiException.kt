package com.amit_kundu_io.ai_sdk.core


/**
 * Base exception type for MyAi SDK.
 */
open class MyAiException(message: String) : Exception(message)

/**
 * Thrown when SDK is accessed before initialization.
 */
class MyAiNotInitializedException :
    MyAiException("MyAi SDK not initialized. Call MyAi.initialize() first.")
