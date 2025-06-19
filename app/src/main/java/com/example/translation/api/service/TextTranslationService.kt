package com.example.translation.api.service

import com.example.translation.api.model.TextRequest
import com.example.translation.api.TranslationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TextTranslationService {
    @POST("translate") // 百度API端点是/translate
    suspend fun translateText(
        @Body request: TextRequest
    ): Response<TranslationResponse>
}