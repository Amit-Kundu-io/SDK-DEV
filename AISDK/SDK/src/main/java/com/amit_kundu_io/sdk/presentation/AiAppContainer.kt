package com.amit_kundu_io.sdk.presentation

import android.content.Context
import com.amit_kundu_io.sdk.core.AiConfig
import com.amit_kundu_io.sdk.core.HttpClientFactory
import com.amit_kundu_io.sdk.core.Logger
import com.amit_kundu_io.sdk.data.AiApiService
import com.amit_kundu_io.sdk.data.AiRepository
import com.amit_kundu_io.sdk.data.AiRepositoryImpl
import com.amit_kundu_io.sdk.domain.UseCaseFactory
import io.ktor.client.HttpClient
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Internal SDK dependency graph.
 *
 * Firebase / Razorpay style manual DI.
 */
internal class AiAppContainer private constructor(
    context: Context,
    private val config: AiConfig
)
    : AutoCloseable {

    internal val appContext: Context = context.applicationContext

    private val closed = AtomicBoolean(false)

    /**
     * Shared process HttpClient.
     */
    private val clientDelegate =
        lazy(
            LazyThreadSafetyMode.SYNCHRONIZED
        ) {

            HttpClientFactory.create(
                config = config
            )
        }

    internal val client: HttpClient
        get() {

            requireOpen()
            return clientDelegate.value
        }

    internal val apiService:
            AiApiService by lazy(
        LazyThreadSafetyMode.SYNCHRONIZED
    ) {

        AiApiService(
            client = client,
            baseUrl = ""
        )
    }

    internal val repository:
            AiRepository by lazy(
        LazyThreadSafetyMode.SYNCHRONIZED
    ) {

        AiRepositoryImpl(
            api = apiService
        )
    }

    internal val useCases by lazy(
        LazyThreadSafetyMode.SYNCHRONIZED
    ) {
        UseCaseFactory(repository = repository)
    }

    override fun close() {

        if (
            !closed.compareAndSet(false, true)
        ) {
            return
        }

        runCatching {

            if (
                clientDelegate.isInitialized()
            ) {

                client.close()
            }

        }.onSuccess {

            Logger.i("AiAppContainer closed.")

        }.onFailure {

            Logger.e("Container cleanup failed.", it)
        }
    }

    private fun requireOpen() {

        check(
            !closed.get()
        ) {

            """
            SDK container already closed.

            Call SDK.initialize()
            before using SDK APIs.
            """.trimIndent()
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
         * Singleton graph creation.
         */
        fun getOrCreate(
            context: Context,
            config: AiConfig
        ): AiAppContainer {

            instance?.let {

                validateConfig(
                    config
                )

                return it
            }

            return synchronized(this) {

                instance?.let {
                    return it
                }

                validateConfig(
                    config
                )

                AiAppContainer(
                    context =
                        context.applicationContext,
                    config = config
                ).also {

                    instance = it
                    initializedConfig =
                        config

                    Logger.i(
                        "AiAppContainer initialized."
                    )
                }
            }
        }

        /**
         * Destroy graph.
         */
        fun clear() {

            synchronized(this) {

                val current =
                    instance
                        ?: return

                current.close()

                instance = null
                initializedConfig = null

                Logger.i(
                    "AiAppContainer cleared."
                )
            }
        }

        /**
         * Prevent unsafe reinit.
         */
        private fun validateConfig(
            newConfig: AiConfig
        ) {

            val existing =
                initializedConfig
                    ?: return

            require(
                existing == newConfig
            ) {

                """
                SDK already initialized
                with different config.

                Call shutdown()
                before reinitialization.
                """.trimIndent()
            }
        }
    }
}