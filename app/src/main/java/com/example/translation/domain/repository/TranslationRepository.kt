package com.example.translation.domain.repository

import com.example.translation.api.model.TranslationRecord
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    suspend fun translateText(
        text: String,
        sourceLang: String,  // 统一为sourceLang
        targetLang: String   // 统一为targetLang
    ): String

    fun getHistory(): Flow<List<com.example.translation.api.model.TranslationRecord>>
}