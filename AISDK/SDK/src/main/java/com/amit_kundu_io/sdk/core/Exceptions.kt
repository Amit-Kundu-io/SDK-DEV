package com.amit_kundu_io.sdk.core


sealed class MyAiException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(
    message,
    cause
)

class ValidationException(
    message: String
) : MyAiException(message)

class NotInitializedException :
    MyAiException(
        "SDK not initialized."
    )

class NetworkException(
    cause: Throwable
) : MyAiException(
    "Network failure.",
    cause
)

class TimeoutException(
    cause: Throwable
) : MyAiException(
    "Request timeout.",
    cause
)

class SerializationFailureException(
    cause: Throwable
) : MyAiException(
    "Serialization failure.",
    cause
)

class ApiException(
    val code: Int,
    message: String
) : MyAiException(message)