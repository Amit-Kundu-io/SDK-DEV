package com.amit_kundu_io.ai_sdk.core


import kotlinx.coroutines.CoroutineDispatcher

/**
 * Abstraction for coroutine dispatchers.
 * Allows testability by swapping dispatchers.
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}
