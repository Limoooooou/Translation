package com.example.translation.api

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mockStatic
import java.io.IOException
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import android.util.Log

@ExtendWith(MockitoExtension::class)
class BaiduTranslationApiTest {

    @Mock
    private lateinit var mockOkHttpClient: OkHttpClient

    @Mock
    private lateinit var mockCall: Call

    @Mock
    private lateinit var mockResponse: Response

    @Mock
    private lateinit var mockResponseBody: ResponseBody

    @BeforeEach
    fun setup() {
        // Mock the static Log.d() calls
        mockStatic(Log::class.java).use { mockedLog ->
            mockedLog.`when`<Int> { Log.d(any(), any()) }.thenReturn(0)
        }

        BaiduTranslationApi.setTestClient(mockOkHttpClient)
    }

    @Test
    fun `translateText should return correct translation on successful response`() {
        // 准备测试数据
        val testText = "hello"
        val fromLang = "en"
        val toLang = "zh"
        val jsonResponse = """
        {
            "trans_result": [
                {
                    "src": "hello",
                    "dst": "你好"
                }
            ]
        }
        """.trimIndent()

        // 设置mock行为
        whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.isSuccessful).thenReturn(true)
        whenever(mockResponse.body).thenReturn(mockResponseBody)
        whenever(mockResponseBody.string()).thenReturn(jsonResponse)
        whenever(mockResponseBody.contentType()).thenReturn("application/json".toMediaTypeOrNull())

        // 执行测试
        val result = BaiduTranslationApi.translateText(testText, fromLang, toLang)

        // 验证结果
        assertEquals("你好", result)
    }

    @Test
    fun `translateText should handle HTTP error response`() {
        val testText = "hello"
        val fromLang = "en"
        val toLang = "zh"

        // 设置mock行为
        whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.isSuccessful).thenReturn(false)
        whenever(mockResponse.code).thenReturn(400)
        whenever(mockResponse.message).thenReturn("Bad Request")
        whenever(mockResponse.body).thenReturn(mockResponseBody)
        whenever(mockResponseBody.string()).thenReturn("Error details")

        // 执行并验证异常
        val exception = assertThrows(IOException::class.java) {
            BaiduTranslationApi.translateText(testText, fromLang, toLang)
        }

        assertTrue(exception.message!!.contains("400"))
    }

    @Test
    fun `parseTranslationResponse should return correct translation`() {
        val validJson = """
        {
            "trans_result": [
                {
                    "src": "hello",
                    "dst": "你好"
                }
            ]
        }
        """.trimIndent()

        val result = BaiduTranslationApi.parseTranslationResponseForTest(validJson)
        assertEquals("你好", result)
    }
}