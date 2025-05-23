package com.example.translation.api.service

import com.example.translateapp.api.model.TextRequest
import com.example.translateapp.api.model.TranslationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TextTranslationService {
    @POST("translate/text")
    suspend fun translateText(
        @Body request: TextRequest
    ): Response<TranslationResponse>
}