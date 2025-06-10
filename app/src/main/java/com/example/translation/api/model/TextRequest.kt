package com.example.translation.api.model

import com.google.gson.annotations.SerializedName

data class TextRequest(
    @SerializedName("text")
    val text: String,

    @SerializedName("source_lang")
    val sourceLang: String,

    @SerializedName("target_lang")
    val targetLang: String,

    @SerializedName("appid")
    val appId: String,

    @SerializedName("salt")
    val salt: String,

    @SerializedName("sign")
    val sign: String
)