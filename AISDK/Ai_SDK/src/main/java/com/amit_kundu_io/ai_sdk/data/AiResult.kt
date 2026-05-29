package com.amit_kundu_io.ai_sdk.data


/**
 * Result wrapper for AI operations.
 * Provides success/error handling in a functional style.
 */
sealed class AiResult<out T> {
    data class Success<T>(val data: T) : AiResult<T>()
    data class Error(val exception: Throwable) : AiResult<Nothing>()

    inline fun onSuccess(block: (T) -> Unit): AiResult<T> {
        if (this is Success) block(data)
        return this
    }

    inline fun onError(block: (Throwable) -> Unit): AiResult<T> {
        if (this is Error) block(exception)
        return this
    }
}
