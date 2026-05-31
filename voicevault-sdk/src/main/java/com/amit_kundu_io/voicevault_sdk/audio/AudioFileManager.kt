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

/**
 * Class responsible for managing audio files in the SDK's cache directory.
 * @property context The application context.
 */
class AudioFileManager(
    private val context: Context
) {

    /**
     * Lazily creates and returns the SDK-specific cache directory.
     */
    private val sdkDir: File by lazy {

        File(context.cacheDir, "voicevault").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Creates a new temporary file for audio recording.
     * @return A [File] object pointing to a new .m4a file.
     */
    fun createRecordingFile(): File {
        return File(sdkDir, "audio_${UUID.randomUUID()}.m4a")
    }

    /**
     * Deletes the specified [file].
     * @param file The file to delete.
     * @return True if the file was deleted, false otherwise.
     */
    fun delete(file: File): Boolean {
        return file.exists() && file.delete()
    }

    /**
     * Clears all files in the SDK's cache directory.
     */
    fun clearCache() {
        sdkDir.listFiles()?.forEach { it.delete() }
    }
}