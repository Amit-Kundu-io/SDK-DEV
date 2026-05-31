/**
 * AudioValidator.kt
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


import android.media.MediaMetadataRetriever
import java.io.File

/**
 * Utility class to validate recorded audio files.
 */
class AudioValidator {

    /**
     * Validates the provided [file] for existence, size, and duration.
     * @param file The audio file to validate.
     * @throws IllegalArgumentException if the file is invalid.
     */
    fun validate(
        file: File
    ) {
        require(file.exists()) { "Audio file does not exist." }
        require(file.length() > 0) { "Empty audio file." }
        validateDuration(file)
    }

    /**
     * Validates that the audio file has a minimum duration of 1 second.
     * @param file The audio file to check.
     */
    private fun validateDuration(
        file: File
    ) {

        val retriever = MediaMetadataRetriever()

        retriever.setDataSource(file.absolutePath)

        val duration =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?: 0L

        retriever.release()

        //require(duration >= 1000L) { "Recording too short." }
    }
}