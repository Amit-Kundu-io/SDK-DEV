/**
 * PlaybackState.kt
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

sealed interface PlaybackState {

    data object Idle : PlaybackState

    data object Playing : PlaybackState

    data object Paused : PlaybackState

    data object Preparing : PlaybackState

    data object Completed : PlaybackState

    data class Error(
        val throwable: Throwable
    ) : PlaybackState



}