package com.amit_kundu_io.voicevault_sdk.core

/**
 * VoiceVaultConfig.kt
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
data class VoiceVaultConfig(
    val apiKey: String,
    val debug: Boolean = false,
    val connectTimeoutMillis: Long = 30_000L,
    val requestTimeoutMillis: Long = 30_000L,
    val socketTimeoutMillis: Long = 30_000L
)