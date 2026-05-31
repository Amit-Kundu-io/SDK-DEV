/**
 * Validation.kt
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

package com.amit_kundu_io.voicevault_sdk.core


/**
 * Utility for validating SDK configuration and inputs.
 */
internal object Validation {

    /**
     * Validates the provided [config].
     * @param config The configuration to validate.
     * @throws IllegalArgumentException if the configuration is invalid.
     */
    fun validateConfig(
        config: VoiceVaultConfig
    ) {
        require(config.apiKey.isNotBlank()) { "apiKey cannot be empty." }
    }

}