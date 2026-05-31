package com.amit_kundu_io.voicevault_sdk.core


import android.util.Log

/**
 * Internal logging utility for the SDK.
 * Only logs when [enabled] is true (usually set via [VoiceVaultConfig.debug]).
 */
internal object Logger {

    private const val TAG = "MyAiSDK"

    @Volatile
    private var enabled = false

    /**
     * Initializes the logger.
     * @param debug Whether to enable logging.
     */
    fun initialize(debug: Boolean) { enabled = debug }

    /**
     * Logs a debug message.
     */
    fun d(message: String) {
        if (enabled) {
            Log.d(TAG, message)
        }
    }

    /**
     * Logs an informational message.
     */
    fun i(message: String) {
        if (enabled) {
            Log.i(TAG, message)
        }
    }

    /**
     * Logs an error message and optional [throwable].
     */
    fun e(message: String, throwable: Throwable? = null) {
        if (enabled) {
            Log.e(
                TAG,
                message,
                throwable
            )
        }
    }
}