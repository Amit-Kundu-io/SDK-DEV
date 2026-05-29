package com.amit_kundu_io.sdk.core


import android.net.http.NetworkException
import io.ktor.client.plugins.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

internal suspend inline fun <T> safeApiCall(
    crossinline block: suspend () -> T
): T {

    return try {

        block()

    } catch (e: CancellationException) {

        throw e

    } catch (e: HttpRequestTimeoutException) {

        throw TimeoutException(e)

    } catch (e: SocketTimeoutException) {

        throw TimeoutException(e)

    } catch (e: SerializationException) {

        throw SerializationFailureException(e)

    } catch (e: Exception) {

        throw NetworkException(e)
    }
}