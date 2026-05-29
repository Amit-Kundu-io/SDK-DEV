package com.amit_kundu_io.sdk.domain


import com.amit_kundu_io.sdk.data.AiRepository

internal class UseCaseFactory(
    repository: AiRepository
) {

    val generate by lazy {
        GenerateUseCase(
            repository
        )
    }

//    val chat by lazy {
//        ChatUseCase(
//            repository
//        )
//    }
//
//    val summarize by lazy {
//        SummarizeUseCase(
//            repository
//        )
//    }
}