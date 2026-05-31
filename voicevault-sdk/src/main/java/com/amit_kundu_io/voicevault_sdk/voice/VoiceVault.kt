/**
 * VoiceVault.kt
 *
 * Author      : Amit Kundu
 * Created On  : 31/05/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */

package com.amit_kundu_io.voicevault_sdk.voice

import android.content.Context
import com.amit_kundu_io.voicevault_sdk.audio.PlaybackState
import com.amit_kundu_io.voicevault_sdk.audio.PlaybackTime
import com.amit_kundu_io.voicevault_sdk.core.Logger
import com.amit_kundu_io.voicevault_sdk.core.Validation
import com.amit_kundu_io.voicevault_sdk.core.VoiceVaultConfig
import com.amit_kundu_io.voicevault_sdk.di.VoiceContainer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import java.io.File

/**
 * Main entry point for the VoiceVault SDK.
 * This object provides methods to initialize, record, and play back audio.
 */
object VoiceVault {

    /**
     * The current version of the SDK.
     */
    const val VERSION = "1.0.0"

//    @Volatile
//    private var aiModule: AiModule? = null

    @Volatile
    private var initialized =
        false

    @Volatile
    private var sdkScope = createScope()

    /**
     * Returns whether the SDK has been initialized.
     */
    val isInitialized: Boolean
        get() = initialized

    /**
     * Initializes the VoiceVault SDK with the provided [context] and [config].
     * This must be called before using any other SDK functions.
     *
     * @param context The application context.
     * @param config The configuration for the SDK.
     * @throws Throwable if initialization fails.
     */
    fun initialize(
        context: Context,
        config: VoiceVaultConfig
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
                    VoiceContainer
                        .getOrCreate(
                            context = context,
                            config = config
                        )

                if (!sdkScope.isActive) {
                    sdkScope = createScope()
                }

                //aiModule = AiModule(graph)

                initialized = true

                Logger.i("SDK initialized. v$VERSION")

            } catch (t: Throwable) {

                initialized = false
                //aiModule = null

                Logger.e("SDK initialization failed.", t)

                throw t
            }
        }
    }


// ===== ADD BELOW initialize() =====

    private val graph: VoiceContainer
        get() {

            check(initialized) {

                """
            VoiceVault not initialized.

            Call:
            VoiceVault.initialize(...)
            """.trimIndent()
            }

            return VoiceContainer.instance()
        }


    // ===== Public API surface =====

    /**
     * A [StateFlow] representing the current [PlaybackState].
     */
    val playbackState: StateFlow<PlaybackState>
        get() = graph
            .player
            .playbackState

    /**
     * A [StateFlow] representing the current [PlaybackTime].
     */
    val playbackTime: StateFlow<PlaybackTime>
        get() = graph
            .player
            .playbackTime

    /**
     * Starts a new audio recording session.
     */
    suspend fun startRecording() {

        graph
            .recordingRepository
            .startRecording()
    }

    /**
     * Pauses the current audio recording session.
     */
    suspend fun pauseRecording() {

        graph
            .recordingRepository
            .pauseRecording()
    }

    /**
     * Resumes a paused audio recording session.
     */
    suspend fun resumeRecording() {
        graph
            .recordingRepository
            .resumeRecording()
    }

    /**
     * Stops the current audio recording session and returns the recorded [File].
     * @return The recorded audio file.
     */
    suspend fun stopRecording() = graph.recordingRepository.stopRecording()


    // ===== ADD BELOW Recording API =====

    /**
     * Starts playing the specified audio [file].
     * @param file The audio file to play.
     */
    suspend fun play(file: File) {
        graph
            .playbackRepository
            .play(
                file.absolutePath
            )
    }

    /**
     * Pauses the current audio playback.
     */
    suspend fun pausePlayback() {
        graph.playbackRepository.pause()
    }

    /**
     * A [StateFlow] indicating whether the SDK is currently recording.
     */
    val isRecording: StateFlow<Boolean>
        get() = graph.recordingRepository.isRecording

    /**
     * A [StateFlow] representing the current recording duration in milliseconds.
     */
    val recordingTime: StateFlow<Long>
        get() = graph.recordingRepository.recordingTime

    /**
     * Resumes the paused audio playback.
     */
    suspend fun resumePlayback() {

        graph
            .playbackRepository
            .resume()
    }

    /**
     * Stops the audio playback.
     */
    suspend fun stopPlayback() {

        graph
            .playbackRepository
            .stop()
    }




    // ===== ADD BELOW Playback API =====

//    suspend fun upload(
//        file: java.io.File
//    ) {
//
//        graph
//            .repository
//            .uploadAudio(
//                file
//            )
//    }

    /**
     * Public API surface.
     */
//    val ai: AiModule
//        get() {
//            return aiModule
//                ?: throw NotInitializedException(
//                    """
//                    SDK not initialized.
//
//                    Call:
//
//                    SDK.initialize(...)
//                    """.trimIndent()
//                )
//        }

    /**
     * Shuts down the VoiceVault SDK and releases its resources.
     * Safe for repeated calls.
     */
    fun shutdown() {

        synchronized(this) {

            if (!initialized) { return }

            initialized = false
           // aiModule = null
            VoiceContainer.clear()
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