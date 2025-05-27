package com.example.translateapp.domain.repository

import com.example.translateapp.api.model.TextRequest
import com.example.translateapp.api.model.TranslationResponse
import com.example.translation.api.model.TranslationRecord
import com.example.translation.api.service.TextTranslationService
import com.example.translation.local.dao.TranslationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class RealTranslationRepository @Inject constructor(
    private val textTranslationService: TextTranslationService,
    private val translationDao: TranslationDao // 假设您有这个 DAO
) : TranslationRepository {

    override suspend fun translateText(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String {
        val request = TextRequest(
            text = text,
            sourceLang = sourceLang,
            targetLang = targetLang
        )
        val response = textTranslationService.translateText(request)
        if (!response.isSuccessful) {
            throw IOException("Translation failed with code ${response.code()}")
        }
        val translatedData = response.body() ?: throw IOException("Empty response body")
        // 保存翻译记录到数据库
        translationDao.insertRecord(
            TranslationRecord(
                sourceText = text,
                sourceLanguage = sourceLang,
                targetText = translatedData.translatedText,
                targetLanguage = targetLang,
                timestamp = System.currentTimeMillis()
            )
        )
        return translatedData.translatedText
    }

    override fun getHistory(): Flow<List<TranslationRecord>> {
        return translationDao.getAllHistory()
    }
}
