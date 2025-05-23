package com.example.translateapp.data.repository

import com.example.translateapp.api.model.TextRequest
import com.example.translateapp.domain.repository.TranslationRepository
import com.example.translation.api.model.TranslationRecord
import com.example.translation.api.service.TextTranslationService
import com.example.translation.local.dao.TranslationDao
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationRepositoryImpl @Inject constructor(
    private val remote: TextTranslationService,
    private val local: TranslationDao
) : TranslationRepository {

    override suspend fun translateText(
        text: String,
        srcLang: String,
        tgtLang: String
    ): String {
        val response = remote.translateText(TextRequest(text, srcLang, tgtLang))
        val translatedText = response.body()?.translatedText ?: throw IOException("Translation failed")

        val recordId = local.insertRecord(
            TranslationRecord(
                sourceText = text,
                sourceLanguage = srcLang,
                targetText = translatedText,
                targetLanguage = tgtLang
            )
        )

        return translatedText
    }

    override fun getHistory(): Flow<List<TranslationRecord>> {
        return local.getAllHistory()
    }
}