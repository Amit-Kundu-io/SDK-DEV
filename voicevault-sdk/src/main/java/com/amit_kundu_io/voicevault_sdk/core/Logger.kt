package com.amit_kundu_io.voicevault_sdk.core


import android.util.Log

internal object Logger {

    private const val TAG = "MyAiSDK"

    @Volatile
    private var enabled = false

    fun initialize(
        debug: Boolean
    ) {
        enabled = debug
    }

    fun d(
        message: String
    ) {
        if (enabled) {
            Log.d(TAG, message)
        }
    }

    fun i(
        message: String
    ) {
        if (enabled) {
            Log.i(TAG, message)
        }
    }

    fun e(
        message: String,
        throwable: Throwable? = null
    ) {
        if (enabled) {
            Log.e(
                TAG,
                message,
                throwable
            )
        }
    }
}