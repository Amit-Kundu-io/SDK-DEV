/**
 * NetworkResource.kt
 *
 * Author      : Amit Kundu
 * Created On  : 30/05/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */

package com.amit_kundu_io.voicevault_sdk.core

sealed class NetworkResource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResource<T>(data)
    class Error<T>(message: String?, data: T? = null) : NetworkResource<T>(data, message)
    class Loading<T>(data: T? = null) : NetworkResource<T>(data)
}

