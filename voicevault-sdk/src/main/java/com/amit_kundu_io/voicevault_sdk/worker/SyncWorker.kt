/**
 * SyncWorker.kt
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


import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.amit_kundu_io.voicevault_sdk.core.NetworkResource
import com.amit_kundu_io.voicevault_sdk.di.VoiceContainer
import kotlinx.io.IOException
import java.io.File

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {
    private val useCase by lazy { VoiceContainer.instance().useCases.uploadAudioUseCase }

    override suspend fun doWork(): Result {

        Log.d("FILE_UPLOADING", "doWork: Start Worker")


        val id = inputData.getString(WorkerKeys.USER_ID)
        val filePath = inputData.getString(WorkerKeys.FILE_PATH)

        if (id.isNullOrBlank() || filePath.isNullOrBlank()) { return Result.failure() }

        val file = File(filePath)

        if (!file.exists()) {
            return Result.failure()
        }

        return runCatching { useCase(id = id, file = file) }.fold(
            onSuccess = { result ->
                when (result){
                    is NetworkResource.Error<*> ->{Result.retry()}
                    is NetworkResource.Loading<*> ->{Result.retry()}
                    is NetworkResource.Success<*> -> {Result.success()}
                }
            },

            onFailure = { throwable ->
                when (throwable) {
                    is IOException -> Result.retry()
                    else -> Result.failure()
                }
            }
        )
    }


    private fun createForegroundInfo(progress: String): ForegroundInfo {

        val notification =
            NotificationCompat.Builder(applicationContext, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle("Uploading Audio")
                .setContentText(progress)
                .setOngoing(true)
                .build()

        return ForegroundInfo(1001, notification)
    }
}