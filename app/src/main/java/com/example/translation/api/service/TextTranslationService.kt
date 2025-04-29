package com.example.translation.api.service

import android.view.translation.TranslationResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TextTranslationService {
    @POST("translate/text")
    suspend fun translateText(
        @Body request: TextRequest
    ): Response<TranslationResponse>

    // 请求体模型移至 api/model/TextRequest.kt
}

// api/model/TextRequest.kt
data class TextRequest(
    @SerializedName("text") val text: String,
    @SerializedName("source_lang") val sourceLang: String,
    @SerializedName("target_lang") val targetLang: String
)