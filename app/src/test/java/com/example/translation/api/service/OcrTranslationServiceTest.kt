package com.example.translation.api.service

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class OcrTranslationServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: OcrTranslationService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OcrTranslationService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testAutoDetectLanguageTranslation() {
        val mockResponse = """
            {
                "translated_text": "Hello",
                "sourceLang": "zh",
                "targetLang": "en",
                "detectedLanguage": "zh"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        val imagePart = createMockImagePart()

        val call = service.translateImage(
            image = imagePart,
            targetLang = "en"
        )
        val response = call.execute()

        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals("Hello", body.translatedText)
            assertEquals("zh", body.sourceLang)
            assertEquals("en", body.targetLang)
            assertEquals("zh", body.detectedLanguage)
        }
    }

    private fun createMockImagePart(): MultipartBody.Part {
        val file = File.createTempFile("test", ".jpg").apply {
            writeBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
        }
        return MultipartBody.Part.createFormData(
            "image",
            file.name,
            file.readBytes().toRequestBody("image/jpeg".toMediaType())
        )
    }
}