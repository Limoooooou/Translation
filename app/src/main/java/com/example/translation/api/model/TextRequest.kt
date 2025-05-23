package com.example.translateapp.api.model

import com.google.gson.annotations.SerializedName

data class TextRequest(
    @SerializedName("text")
    val text: String,

    @SerializedName("source_lang")
    val sourceLang: String,

    @SerializedName("target_lang")
    val targetLang: String
)