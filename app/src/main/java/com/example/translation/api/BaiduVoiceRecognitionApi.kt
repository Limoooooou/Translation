package com.example.translation.api

import android.util.Base64
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

object BaiduVoiceRecognitionApi {
    private const val APP_ID = "20250609002377601"
    private const val SECRET_KEY = "bRZ7m1bV5nztVBsQdhF2"
    var okHttpClient = OkHttpClient()

    // 语音翻译支持的语种代码映射
    private val languageCodeMap = mapOf(
        "中文" to "zh",
        "英文" to "en",
        "日文" to "jp",
        "韩文" to "kor",
        "德语" to "de"
    )

    /**
     * 语音翻译（将语音文件从源语言翻译为目标语言）
     * @param file 语音文件
     * @param fromLang 源语言（如"中文"）
     * @param toLang 目标语言（如"英文"）
     */
    suspend fun translateVoice(
        file: File,
        fromLang: String,
        toLang: String
    ): Pair<String, String> { // 返回 Pair<识别文本, 翻译结果>
        val from = languageCodeMap[fromLang] ?: "auto"
        val to = languageCodeMap[toLang] ?: "en"

        // 生成随机盐值
        val salt = System.currentTimeMillis().toString()

        // 读取语音文件并转为Base64
        val audioData = file.readBytes()
        val base64Audio = Base64.encodeToString(audioData, Base64.NO_WRAP)

        // 生成签名（百度翻译标准鉴权方式）
        val sign = md5("$APP_ID$base64Audio$salt$SECRET_KEY")

        val url = "https://fanyi-api.baidu.com/api/trans/v2/voicetrans" +
                "?appid=$APP_ID" +
                "&from=$from" +
                "&to=$to" +
                "&salt=$salt" +
                "&sign=$sign"

        // 构建请求体
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("audio", file.name,
                RequestBody.create("audio/wav".toMediaTypeOrNull(), file))
            .build()

        // 发送请求
        return try {
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Pair("", "语音翻译失败: HTTP ${response.code}")
                }
                val responseBody = response.body?.string() ?: return Pair("", "响应体为空")
                parseTranslationResult(responseBody)
            }
        } catch (e: Exception) {
            Log.e("VoiceTranslation", "翻译异常: ${e.message}")
            Pair("", "翻译错误: ${e.message}")
        }
    }

    // MD5加密工具函数（用于生成签名）
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // 解析翻译结果
    private fun parseTranslationResult(jsonResponse: String): Pair<String, String> {
        return try {
            val jsonObject = JSONObject(jsonResponse)
            if (jsonObject.has("error_code")) {
                return Pair("", "错误 ${jsonObject.getInt("error_code")}: ${jsonObject.getString("error_msg")}")
            }

            val result = jsonObject.getJSONObject("result")
            val srcText = result.getString("src") // 识别出的原文
            val dstText = result.getString("dst") // 翻译结果

            Pair(srcText, dstText)
        } catch (e: Exception) {
            Pair("", "解析翻译结果失败: ${e.message}")
        }
    }
}