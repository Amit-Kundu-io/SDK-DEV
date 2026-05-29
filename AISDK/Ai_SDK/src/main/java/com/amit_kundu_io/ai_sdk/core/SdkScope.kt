package com.amit_kundu_io.ai_sdk.core


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Global coroutine scope for SDK operations.
 * Uses SupervisorJob to isolate failures.
 */
object SdkScope {
    val scope = CoroutineScope(SupervisorJob() + DefaultDispatcherProvider.io)
}
