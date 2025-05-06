package com.example.translation.api.service

import android.view.translation.TranslationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SpeechTranslationService {
    @Multipart
    @POST("translate/speech")
    suspend fun recognizeSpeech(
        @Part audio: MultipartBody.Part,
        @Part("source_lang") sourceLang: String,
        @Part("target_lang") targetLang: String
    ): Response<TranslationResponse>
}