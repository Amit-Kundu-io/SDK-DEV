package com.amit_kundu_io.sdk.data


import kotlinx.serialization.Serializable

//@Serializable
internal data class GenerateRequestDto(

    val model: String,

    val prompt: String
)