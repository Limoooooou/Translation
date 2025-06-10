package com.example.translation.domain.repository

import com.example.translation.api.model.TextRequest
import com.example.translation.api.service.TextTranslationService
import com.example.translation.util.BaiduSignUtil
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TranslationRepositoryImpl @Inject constructor(
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
            throw IOException("API error: ${response.code()}")
        }

        return response.body()?.transResult?.firstOrNull()?.dst
            ?: throw IOException("Empty translation result")
    }

    override fun getHistory(): Flow<List<com.example.translation.api.model.TranslationRecord>> {
        // 暂时返回空流，需根据实际数据库实现
        return flowOf(emptyList())
    }
}