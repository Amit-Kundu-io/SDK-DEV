/**
 * Playback.kt
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

/**
 * Data class representing the playback time information.
 * @property totalTime The total duration of the audio in milliseconds.
 * @property time The current playback position in milliseconds.
 */
data class PlaybackTime(val totalTime: Long, val time: Long)