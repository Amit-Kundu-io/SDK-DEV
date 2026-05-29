package com.amit_kundu_io.sdk.core


data class AiConfig(

    val apiKey: String,

    val baseUrl: String = "",

    val debug: Boolean = false,

    val connectTimeoutMillis: Long =
        30_000L,

    val requestTimeoutMillis: Long =
        30_000L,

    val socketTimeoutMillis: Long =
        30_000L
)