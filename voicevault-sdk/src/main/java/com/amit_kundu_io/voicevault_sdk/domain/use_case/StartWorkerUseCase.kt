/**
 * StartWorkerUseCase.kt
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

package com.amit_kundu_io.voicevault_sdk.domain.use_case

import android.content.Context
import android.util.Log
import com.amit_kundu_io.voicevault_sdk.worker.startSyncWorker


internal class StartWorkerUseCase(
   private val context: Context
) {
      fun invoke(
        id: String,
        file: String
    ) {
          Log.d("FILE_UPLOADING", "invoke: StartWorkerUseCase")
        startSyncWorker(
           context =  context,
            id = id,
            filePath = file,
        )
    }
}