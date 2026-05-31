/**
 * NotificationHelper.kt
 *
 * Author      : Amit Kundu
 * Created On  : 01/06/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */

package com.amit_kundu_io.voicevault_sdk.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationHelper {

    const val CHANNEL_ID =
        "voice_upload"

    fun createChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(

                    CHANNEL_ID,

                    "Audio Upload",

                    NotificationManager
                        .IMPORTANCE_LOW
                )

            context
                .getSystemService(
                    NotificationManager::class.java
                )
                .createNotificationChannel(
                    channel
                )
        }
    }
}