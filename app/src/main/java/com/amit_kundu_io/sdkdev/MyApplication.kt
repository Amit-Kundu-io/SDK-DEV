package com.amit_kundu_io.sdkdev

import android.app.Application
import com.amit_kundu_io.ai_sdk.presentation.MyAi
import com.amit_kundu_io.sdk.core.AiConfig
import com.amit_kundu_io.sdk.presentation.SDK
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
           // MyAi.initialize(apiKey = "YOUR_KEY")

            SDK.initialize(applicationContext, config = AiConfig("Amit478ku65du"))

            //modules(appModule)
        }
    }
}