/**
 * AudioFileManager.kt
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

package com.amit_kundu_io.voicevault_sdk.audio


import android.content.Context
import java.io.File
import java.util.UUID

class AudioFileManager(
    private val context: Context
) {

    private val sdkDir: File by lazy {

        File(
            context.cacheDir,
            "voicevault"
        ).apply {

            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun createRecordingFile(): File {

        return File(
            sdkDir,
            "audio_${UUID.randomUUID()}.m4a"
        )
    }

    fun delete(
        file: File
    ): Boolean {
        return file.exists() && file.delete()
    }

    fun clearCache() {
        sdkDir.listFiles()?.forEach { it.delete() }
    }
}