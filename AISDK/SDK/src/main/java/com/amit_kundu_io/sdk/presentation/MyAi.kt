package com.amit_kundu_io.sdk.presentation


import android.content.Context
import com.amit_kundu_io.sdk.core.AiConfig
import com.amit_kundu_io.sdk.core.AiResult
import com.amit_kundu_io.sdk.core.Logger
import com.amit_kundu_io.sdk.core.NotInitializedException
import com.amit_kundu_io.sdk.core.Validation
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean



/**
 * Public SDK Entry Point.
 *
 * Thread-safe singleton.
 *
 * Owns:
 * - SDK lifecycle
 * - Dependency graph access
 * - Coroutine scope lifecycle
 */
object SDK {

    @Volatile
    private var container:
            AiAppContainer? = null

    private val initialized =
        AtomicBoolean(false)

    @Volatile
    private var sdkScope =
        createScope()

    /**
     * Initialize SDK.
     *
     * Safe for concurrent calls.
     */
    fun initialize(
        context: Context,
        config: AiConfig
    ) {

        if (initialized.get()) return

        synchronized(this) {

            if (initialized.get()) return

            Validation.validateConfig(
                config
            )

            Logger.initialize(
                config.debug
            )

            container =
                AiAppContainer
                    .getOrCreate(
                        context = context,
                        config = config
                    )

            if (!sdkScope.isActive) {
                sdkScope =
                    createScope()
            }

            initialized.set(true)

            Logger.i(
                "MyAi initialized."
            )
        }
    }

    /**
     * Public access to SDK features.
     */
    val ai: AiModule
        get() = AiModule(
            requireContainer()
        )

    /**
     * Destroy SDK resources.
     *
     * Safe for repeated calls.
     */
    fun shutdown() {

        synchronized(this) {

            if (!initialized.get()) {
                return
            }

            initialized.set(false)

            container = null

            AiAppContainer.clear()

            sdkScope.cancel()

            Logger.i(
                "MyAi shutdown."
            )
        }
    }

    /**
     * Fast container lookup.
     */
    private fun requireContainer():
            AiAppContainer {

        return container
            ?: throw NotInitializedException()
    }

    /**
     * Dedicated SDK scope.
     */
    private fun createScope():
            CoroutineScope {

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