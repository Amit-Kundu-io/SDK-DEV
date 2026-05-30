/**
 * PlaybackRepositoryImpl.kt
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

package com.amit_kundu_io.voicevault_sdk.data.repoImpl


import com.amit_kundu_io.voicevault_sdk.audio.AudioPlayer
import com.amit_kundu_io.voicevault_sdk.data.repo.PlaybackRepository

internal class
PlaybackRepositoryImpl(

    private val player:
    AudioPlayer
) : PlaybackRepository {

    override suspend fun play(
        path: String
    ) {

        player.play(path)
    }

    override suspend fun pause() {

        player.pause()
    }

    override suspend fun resume() {

        player.resume()
    }

    override suspend fun stop() {

        player.stop()
    }
}