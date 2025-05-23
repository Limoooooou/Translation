package com.example.translateapp.api.model

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
    @SerializedName("translated_text")
    val translatedText: String,
    @SerializedName("sourceLang")
    val sourceLang: String = "",
    @SerializedName("targetLang")
    val targetLang: String = "",
    @SerializedName("detectedLanguage")
    val detectedLanguage: String? = null
)