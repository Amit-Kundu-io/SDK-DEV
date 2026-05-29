package com.amit_kundu_io.ai_sdk.core


import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of [DispatcherProvider].
 */
object DefaultDispatcherProvider : DispatcherProvider {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
}
