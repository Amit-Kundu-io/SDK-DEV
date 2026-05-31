/**
 * VoiceContainer.kt
 *
 * Author      : Amit Kundu
 * Created On  : 30/05/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */

package com.amit_kundu_io.voicevault_sdk.di

import android.content.Context
import com.amit_kundu_io.voicevault_sdk.audio.AudioFileManager
import com.amit_kundu_io.voicevault_sdk.audio.AudioPlayer
import com.amit_kundu_io.voicevault_sdk.audio.AudioPlayerImpl
import com.amit_kundu_io.voicevault_sdk.audio.AudioRecorder
import com.amit_kundu_io.voicevault_sdk.audio.AudioRecorderImpl
import com.amit_kundu_io.voicevault_sdk.audio.AudioValidator
import com.amit_kundu_io.voicevault_sdk.core.Logger
import com.amit_kundu_io.voicevault_sdk.core.VoiceVaultConfig
import com.amit_kundu_io.voicevault_sdk.data.repo.Repository
import com.amit_kundu_io.voicevault_sdk.data.repoImpl.PlaybackRepositoryImpl
import com.amit_kundu_io.voicevault_sdk.data.repoImpl.RecordingRepositoryImpl
import com.amit_kundu_io.voicevault_sdk.data.repoImpl.RepositoryImpl
import com.amit_kundu_io.voicevault_sdk.data.service.ApiService
import com.amit_kundu_io.voicevault_sdk.domain.UseCaseFactory
import com.amit_kundu_io.voicevault_sdk.worker.NotificationHelper
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.getValue


/**
 * Internal dependency container for the VoiceVault SDK.
 * Manages the lifecycle of singletons used across the SDK.
 */
internal class VoiceContainer private constructor(
    context: Context,
    private val config: VoiceVaultConfig
) : AutoCloseable {

    internal val appContext: Context = context.applicationContext

    private val closed = AtomicBoolean(false)

    private val sdkScope = CoroutineScope(SupervisorJob())

    /**
     * Shared process HttpClient.
     */
    private val clientDelegate = lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        HttpClientFactory.create(config = config)
        }

    internal val client: HttpClient
        get() {
            requireOpen()
            return clientDelegate.value
        }

    internal val apiService: ApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ApiService(
            client = client,
            baseUrl = ""
        )
    }

    internal val repository: Repository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RepositoryImpl(
            api = apiService
        )
    }

    /***
     *Voice Di
     */

    internal val fileManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AudioFileManager(context = appContext)
    }

    internal val validator by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AudioValidator()
    }

    internal val recorder: AudioRecorder by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AudioRecorderImpl(
            fileManager = fileManager,
            context = appContext
        )
    }

    internal val player: AudioPlayer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AudioPlayerImpl(
            sdkScope
        )
    }


    internal val recordingRepository by lazy {
        RecordingRepositoryImpl(
            recorder = recorder,
            validator = validator
        )
    }

    internal val playbackRepository by lazy { PlaybackRepositoryImpl(player = player) }



    internal val useCases by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        UseCaseFactory(repository = repository, context = context)
    }



    override fun close() {

        if (!closed.compareAndSet(false, true)) {
            return
        }

        runCatching {
            if (clientDelegate.isInitialized()) {
                client.close()
            }

        }.onSuccess {

            Logger.i("AiAppContainer closed.")

        }.onFailure {

            Logger.e("Container cleanup failed.", it)
        }
    }

    private fun requireOpen() {

        check(!closed.get()) {

            """
            SDK container already closed.

            Call SDK.initialize()
            before using SDK APIs.
            """.trimIndent()
        }
    }

    companion object {

        @Volatile
         private var instance: VoiceContainer? = null

        @Volatile
        private var initializedConfig: VoiceVaultConfig? = null

        /**
         * Singleton graph creation.
         */
        fun getOrCreate(
            context: Context,
            config: VoiceVaultConfig
        ): VoiceContainer {

            instance?.let {

                validateConfig(config)

                return it
            }


            return synchronized(this) {

                instance?.let { return it }

                validateConfig(config)

                NotificationHelper.createChannel(context)


                VoiceContainer(
                    context = context.applicationContext,
                    config = config
                ).also {
                    instance = it
                    initializedConfig = config

                    Logger.i("AiAppContainer initialized.")
                }
            }
        }




        fun instance(): VoiceContainer {

            return requireNotNull(instance) {

                """
        VoiceContainer
        not initialized.

        Call:
        VoiceVault.initialize(...)
        """.trimIndent()
            }
        }

        /**
         * Destroy graph.
         */
        fun clear() {

            synchronized(this) {

                val current = instance ?: return

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
            newConfig: VoiceVaultConfig
        ) {

            val existing = initializedConfig ?: return

            require(existing == newConfig) {

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