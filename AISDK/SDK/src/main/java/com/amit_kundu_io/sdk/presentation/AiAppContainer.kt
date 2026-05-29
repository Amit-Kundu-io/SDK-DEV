package com.amit_kundu_io.sdk.presentation

import android.content.Context
import com.amit_kundu_io.sdk.core.AiConfig
import com.amit_kundu_io.sdk.core.HttpClientFactory
import com.amit_kundu_io.sdk.core.Logger
import com.amit_kundu_io.sdk.data.AiApiService
import com.amit_kundu_io.sdk.data.AiRepository
import com.amit_kundu_io.sdk.data.AiRepositoryImpl
import com.amit_kundu_io.sdk.domain.GenerateUseCase
import com.amit_kundu_io.sdk.domain.UseCaseFactory
import io.ktor.client.HttpClient
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


/**
 * Internal SDK dependency container.
 *
 * Production-ready singleton dependency graph.
 *
 * Owns:
 * - HttpClient lifecycle
 * - Network layer
 * - Repository layer
 * - UseCases
 *
 * Characteristics:
 * - Thread-safe
 * - Lazy initialization
 * - Double-checked singleton
 * - Leak-safe
 * - Resource safe shutdown
 * - Firebase-style internal manual DI
 */
internal class AiAppContainer private constructor(
    context: Context,
    private val config: AiConfig
) : AutoCloseable {

    /**
     * Leak-safe application context.
     */
    private val appContext: Context =
        context.applicationContext

    /**
     * Prevent duplicate cleanup.
     */
    @OptIn(ExperimentalAtomicApi::class)
    private val closed =
        AtomicBoolean(false)

    /**
     * Lazy HttpClient delegate.
     *
     * Expensive resource.
     * Create only when first needed.
     */
    private val clientDelegate = lazy(
        LazyThreadSafetyMode.SYNCHRONIZED
    ) {

        HttpClientFactory.create(
            config = config
        )
    }

    /**
     * Shared singleton HttpClient.
     */
    internal val client: HttpClient
        get() = clientDelegate.value

    /**
     * Network service singleton.
     */
    internal val apiService: AiApiService by lazy(
        LazyThreadSafetyMode.SYNCHRONIZED
    ) {

        AiApiService(
            client = client,
            baseUrl = config.baseUrl
        )
    }

    /**
     * Repository singleton.
     */
    internal val repository: AiRepository by lazy(
        LazyThreadSafetyMode.SYNCHRONIZED
    ) {

        AiRepositoryImpl(
            api = apiService
        )
    }

    /**
     * UseCase.
     */
    internal val useCases by lazy {

        UseCaseFactory(
            repository
        )
    }


    /**
     * Cleanup SDK resources.
     *
     * Safe for multiple calls.
     */
    @OptIn(ExperimentalAtomicApi::class)
    override fun close() {

        if (!closed.compareAndSet(false, true)) {
            return
        }

        try {

            if (clientDelegate.isInitialized()) {
                client.close()
            }

            Logger.i(
                "AiAppContainer closed."
            )

        } catch (t: Throwable) {

            Logger.e(
                "Container cleanup failed.",
                t
            )
        }
    }

    companion object {

        @Volatile
        private var instance:
                AiAppContainer? = null

        @Volatile
        private var initializedConfig:
                AiConfig? = null

        /**
         * Thread-safe singleton creation.
         *
         * Prevents:
         * - double initialization
         * - race conditions
         * - different config reinit bugs
         */
        fun getOrCreate(
            context: Context,
            config: AiConfig
        ): AiAppContainer {

            instance?.let { existing ->

                if (
                    initializedConfig != null &&
                    initializedConfig != config
                ) {

                    throw IllegalStateException(
                        """
                        SDK already initialized
                        with different config.

                        Call shutdown()
                        before reinitialization.
                        """.trimIndent()
                    )
                }

                return existing
            }

            return synchronized(this) {

                instance?.let {
                    return it
                }

                AiAppContainer(
                    context = context.applicationContext,
                    config = config
                ).also {

                    instance = it
                    initializedConfig = config

                    Logger.i(
                        "AiAppContainer initialized."
                    )
                }
            }
        }

        /**
         * Destroy dependency graph.
         */
        fun clear() {

            synchronized(this) {

                instance?.close()

                instance = null
                initializedConfig = null

                Logger.i(
                    "AiAppContainer cleared."
                )
            }
        }
    }
}