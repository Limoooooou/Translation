package com.example.translation.api.model

import com.google.gson.annotations.SerializedName

data class TextRequest(
    @SerializedName("q")
    val text: String,

    @SerializedName("from")
    val sourceLang: String,

    @SerializedName("to")
    val targetLang: String,

    @SerializedName("appid")
    val appId: String,

    @SerializedName("salt")
    val salt: String,

    @SerializedName("sign")
    val sign: String
)