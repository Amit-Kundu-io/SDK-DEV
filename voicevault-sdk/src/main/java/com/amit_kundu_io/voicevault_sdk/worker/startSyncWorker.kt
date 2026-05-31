package com.amit_kundu_io.voicevault_sdk.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

internal fun startSyncWorker(
    context: Context,
    id: String,
    filePath: String
) {

    val constraints =

        Constraints.Builder().build()

    val inputData = workDataOf(WorkerKeys.USER_ID to id, WorkerKeys.FILE_PATH to filePath)

    val request =
        OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(inputData)
            .setConstraints(
                constraints
            )

            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .build()

    WorkManager
        .getInstance(context)
        .enqueueUniqueWork(
            "voice_upload_worker",
            ExistingWorkPolicy.REPLACE,
            request
        )
}