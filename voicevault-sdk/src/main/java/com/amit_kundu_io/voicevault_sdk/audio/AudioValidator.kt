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

class AudioValidator {

    fun validate(
        file: File
    ) {
        require(file.exists()) { "Audio file does not exist." }
        require(file.length() > 0) { "Empty audio file." }
        validateDuration(file)
    }

    private fun validateDuration(
        file: File
    ) {

        val retriever =
            MediaMetadataRetriever()

        retriever.setDataSource(
            file.absolutePath
        )

        val duration =
            retriever
                .extractMetadata(
                    MediaMetadataRetriever
                        .METADATA_KEY_DURATION
                )
                ?.toLongOrNull()
                ?: 0L

        retriever.release()

        require(
            duration >= 1000L
        ) {

            "Recording too short."
        }
    }
}