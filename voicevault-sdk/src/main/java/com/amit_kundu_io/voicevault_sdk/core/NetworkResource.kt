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

/**
 * A sealed class representing the status of a network request.
 * @param T The type of data being returned.
 * @property data The data returned from the request, if any.
 * @property message An error message or status description.
 */
sealed class NetworkResource<T>(val data: T? = null, val message: String? = null) {
    /**
     * Represents a successful network request.
     */
    class Success<T>(data: T) : NetworkResource<T>(data)

    /**
     * Represents a failed network request.
     */
    class Error<T>(message: String?, data: T? = null) : NetworkResource<T>(data, message)

    /**
     * Represents a network request that is currently in progress.
     */
    class Loading<T>(data: T? = null) : NetworkResource<T>(data)
}

