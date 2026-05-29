package com.amit_kundu_io.sdk.core


sealed interface AiResult<out T> {

    data class Success<T>(
        val data: T
    ) : AiResult<T>

    data class Error(
        val throwable: Throwable
    ) : AiResult<Nothing>
}