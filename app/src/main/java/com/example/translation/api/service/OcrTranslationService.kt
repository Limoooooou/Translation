package com.example.translation.api.service

import android.view.translation.TranslationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrTranslationService {
    @Multipart
    @POST("translate/ocr")
    suspend fun recognizeImage(
        @Part image: MultipartBody.Part,
        @Part("source_lang") sourceLang: String,
        @Part("target_lang") targetLang: String
    ): Response<TranslationResponse>
}

// 数据模型移至 api/model/TranslationResponse.kt