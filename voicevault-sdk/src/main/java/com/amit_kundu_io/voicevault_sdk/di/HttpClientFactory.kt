package com.amit_kundu_io.voicevault_sdk.di


import com.amit_kundu_io.voicevault_sdk.core.VoiceVaultConfig
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Factory for creating and configuring the Ktor [HttpClient] used by the SDK.
 */
internal object HttpClientFactory {

    /**
     * Creates a new [HttpClient] instance with the provided [config].
     * Configures timeouts, logging, content negotiation, and default headers.
     *
     * @param config The SDK configuration.
     * @return A configured [HttpClient] instance.
     */
    fun create(
        config: VoiceVaultConfig
    ): HttpClient {

        return HttpClient {

            expectSuccess = false

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = false
                        isLenient = true
                        explicitNulls = false
                    }
                )
            }

            install(HttpTimeout) {

                connectTimeoutMillis = config.connectTimeoutMillis
                requestTimeoutMillis = config.requestTimeoutMillis
                socketTimeoutMillis = config.socketTimeoutMillis
            }

            install(Logging) {

               // logger = Logger.DEFAULT

                level = if (config.debug) { LogLevel.ALL } else { LogLevel.NONE }
            }

            defaultRequest {

                contentType(ContentType.Application.Json)

                header(
                    HttpHeaders.Authorization,
                    "Bearer ${config.apiKey}"
                )
            }
        }
    }
}