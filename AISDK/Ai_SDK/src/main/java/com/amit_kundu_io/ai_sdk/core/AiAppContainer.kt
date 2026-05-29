package com.amit_kundu_io.ai_sdk.core


import android.content.Context
import com.amit_kundu_io.ai_sdk.data.AiRepository
import com.amit_kundu_io.ai_sdk.data.AiRepositoryImpl
import com.amit_kundu_io.ai_sdk.domain.use_case.GenerateUseCase
import com.amit_kundu_io.ai_sdk.presentation.AiModel
import io.ktor.client.HttpClient
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

import org.koin.core.context.*
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Internal container for SDK dependencies.
 * Uses Koin for DI and wires repository + use cases.
 */




internal object AiAppContainer {

    private val initialized = AtomicBoolean(false)

    private var ownsKoin = false
    private lateinit var sdkModule: Module

    private lateinit var httpClient: HttpClient
    private lateinit var koin: Koin

    fun initialize(
        apiKey: String
    ) {

        if (!initialized.compareAndSet(false, true)) {
            return
        }

        val config = AiConfig(apiKey)

        httpClient = HttpClient()

        sdkModule = module {

            single { config }

            single { httpClient }

            single<AiRepository> {
                AiRepositoryImpl(
                    client = get(),
                    config = get()
                )
            }

            single {
                GenerateUseCase(get())
            }
        }

        synchronized(this) {

            val existingKoin = GlobalContext.getOrNull()

            if (existingKoin == null) {

                ownsKoin = true

                koin = startKoin {
                    modules(sdkModule)
                }.koin

            } else {

                ownsKoin = false

                loadKoinModules(sdkModule)

                koin = existingKoin
            }
        }
    }

    fun model(): AiModel {

        ensureInitialized()

        return AiModel(
            koin.get()
        )
    }

    fun shutdown() {

        if (!initialized.compareAndSet(true, false)) {
            return
        }

        synchronized(this) {

            runCatching {
                httpClient.close()
            }

            runCatching {

                if (ownsKoin) {
                    stopKoin()
                } else {
                    unloadKoinModules(sdkModule)
                }
            }
        }
    }

    private fun ensureInitialized() {

        if (!initialized.get()) {
            throw MyAiNotInitializedException()
        }
    }
}
