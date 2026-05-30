package com.amit_kundu_io.sdk.presentation

import android.content.Context
import com.amit_kundu_io.sdk.core.AiConfig
import com.amit_kundu_io.sdk.core.Logger
import com.amit_kundu_io.sdk.core.NotInitializedException
import com.amit_kundu_io.sdk.core.Validation
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

/**
 * Public SDK singleton.
 *
 * Firebase / Razorpay style entry point.
 */
object SDK {

    const val VERSION = "1.0.0"

    @Volatile
    private var aiModule: AiModule? = null

    @Volatile
    private var initialized =
        false

    @Volatile
    private var sdkScope = createScope()

    /**
     * Public lifecycle state.
     */
    val isInitialized: Boolean
        get() = initialized

    /**
     * Initialize SDK.
     *
     * Safe for concurrent calls.
     */
    fun initialize(
        context: Context,
        config: AiConfig
    ) {

        if (initialized) {
            return
        }

        synchronized(this) {

            if (initialized) {
                return
            }

            Validation.validateConfig(config)

            try {

                Logger.initialize(config.debug)

                val graph =
                    AiAppContainer
                        .getOrCreate(
                            context = context,
                            config = config
                        )

                if (!sdkScope.isActive) {
                    sdkScope = createScope()
                }

                aiModule = AiModule(graph)

                initialized = true

                Logger.i("SDK initialized. v$VERSION")

            } catch (t: Throwable) {

                initialized = false
                aiModule = null

                Logger.e("SDK initialization failed.", t)

                throw t
            }
        }
    }

    /**
     * Public API surface.
     */
    val ai: AiModule
        get() {
            return aiModule
                ?: throw NotInitializedException(
                    """
                    SDK not initialized.

                    Call:

                    SDK.initialize(...)
                    """.trimIndent()
                )
        }

    /**
     * Shutdown SDK.
     *
     * Safe for repeated calls.
     */
    fun shutdown() {

        synchronized(this) {

            if (!initialized) { return }

            initialized = false
            aiModule = null
            AiAppContainer.clear()
            sdkScope.cancel()

            Logger.i("SDK shutdown complete.")
        }
    }

    /**
     * Dedicated internal scope.
     */
    private fun createScope(): CoroutineScope {

        return CoroutineScope(

            SupervisorJob() +
                    Dispatchers.IO +
                    CoroutineExceptionHandler { _, throwable ->

                        Logger.e(
                            "SDK coroutine failure.",
                            throwable
                        )
                    }
        )
    }
}