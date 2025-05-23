package com.example.translateapp.domain.repository

import com.example.translateapp.api.model.TextRequest
import com.example.translateapp.api.model.TranslationResponse
import com.example.translateapp.data.repository.TranslationRepositoryImpl
import com.example.translation.api.model.TranslationRecord
import com.example.translation.api.service.TextTranslationService
import com.example.translation.local.dao.TranslationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class TranslationRepositoryImplTest {

    // 使用正确的服务接口 TextTranslationService
    private val mockRemoteService = object : TextTranslationService {
        override suspend fun translateText(request: TextRequest): Response<TranslationResponse> {
            return Response.success(
                TranslationResponse(translatedText = "Hello (Translated)")
            )
        }
    }

    // 修正 DAO 实现以匹配接口定义
    private val mockDao = object : TranslationDao {
        private val records = mutableListOf<TranslationRecord>()

        override fun insertRecord(record: TranslationRecord): Long {
            records.add(record)
            return 1L // 返回模拟的插入ID
        }

        override fun getAllHistory(): Flow<List<TranslationRecord>> {
            return flowOf(records.toList())
        }
    }

    @Test
    fun `test translation and history`() = runBlocking {
        val repo = TranslationRepositoryImpl(mockRemoteService, mockDao)

        // 测试翻译功能 - 使用正确的请求格式
        val result = repo.translateText("你好", "zh", "en")
        assertEquals("Hello (Translated)", result)

        // 测试历史记录 - 使用正确的 DAO 方法名
        repo.getHistory().collect { records ->
            assertEquals(1, records.size)
            assertEquals("你好", records[0].sourceText)
            assertEquals("Hello (Translated)", records[0].targetText)
        }
    }
}