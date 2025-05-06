package com.example.translation.domain.repository

import com.example.translation.domain.repository.TranslationRepository
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor() : TranslationRepository {
    override suspend fun translate(text: String): String {
        // 实际网络请求逻辑（示例）
        return "Translated: $text"
    }
}