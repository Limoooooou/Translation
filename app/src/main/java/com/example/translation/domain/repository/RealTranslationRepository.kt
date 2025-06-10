package com.example.translation.domain.repository

import com.example.translation.api.model.TextRequest
import com.example.translation.api.model.TranslationRecord
import com.example.translation.api.service.TextTranslationService
import com.example.translation.local.dao.TranslationDao
import com.example.translation.util.BaiduSignUtil
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.Flow

class RealTranslationRepository @Inject constructor(
    private val translationDao: TranslationDao ,
    private val service: TextTranslationService,
    @Named("appId")private val appId: String,
    @Named("secretKey")private val secretKey: String
) : TranslationRepository {

    override suspend fun translateText(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String {
        val salt = System.currentTimeMillis().toString()
        val sign = BaiduSignUtil.generateSign(appId, text, salt, secretKey)

        val response = service.translateText(
            TextRequest(
                text = text,
                sourceLang = sourceLang,
                targetLang = targetLang,
                appId = appId,
                salt = salt,
                sign = sign
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Translation failed with code ${response.code()}")
        }
        val translatedData = response.body() ?: throw IOException("Empty response body")
        val translatedText = translatedData.transResult.firstOrNull()?.dst
            ?: throw IOException("No translation result")

        // 保存翻译记录到数据库
        translationDao.insertRecord(
            TranslationRecord(
                sourceText = text,
                sourceLanguage = sourceLang,
                targetText = translatedText,
                targetLanguage = targetLang,
                timestamp = System.currentTimeMillis()
            )
        )
        return translatedText
    }

    override fun getHistory(): Flow<List<TranslationRecord>> {
        return translationDao.getAllHistory()
    }
}
