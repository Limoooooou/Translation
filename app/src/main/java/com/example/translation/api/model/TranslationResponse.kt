package com.example.translation.api.model

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
    @SerializedName("from") val sourceLang: String,
    @SerializedName("to") val targetLang: String,
    @SerializedName("trans_result") val transResult: List<TransResult>
)

data class TransResult(
    @SerializedName("src") val src: String,
    @SerializedName("dst") val dst: String
)
