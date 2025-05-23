package com.example.translation.api.service

import com.example.translateapp.api.model.TranslationResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface OcrTranslationService {
    @Multipart
    @POST("translate/ocr")
    fun translateImage(
        @Part image: MultipartBody.Part,
        @Query("sourceLang") sourceLang: String? = null,
        @Query("targetLang") targetLang: String
    ): Call<TranslationResponse>
}

// 数据模型移至 api/model/TranslationResponse.kt