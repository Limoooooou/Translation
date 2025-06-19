package com.example.translation.api

import android.graphics.Bitmap
import android.util.Log
import com.example.translation.api.BaiduSignUtil.generateSign
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Base64

object BaiduImageTranslationApi {
    private const val appId = "20250609002377601"
    private const val secretKey = "bRZ7m1bV5nztVBsQdhF2"
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    /**
     * 执行图片翻译请求
     * @param bitmap 拍摄的图片
     * @param from 源语言代码
     * @param to 目标语言代码
     * @return 翻译结果字符串
     */
    suspend fun translateImage(
        bitmap: Bitmap,
        from: String,
        to: String
    ): String {
        // 验证参数
        if (bitmap.isRecycled) {
            return "图片已失效"
        }
        if (from.isEmpty() || to.isEmpty()) {
            return "源语言和目标语言不能为空"
        }
        if (appId.isEmpty() || secretKey.isEmpty()) {
            return "AppID和密钥不能为空"
        }

        // 生成随机数salt
        val salt = System.currentTimeMillis().toString()

        // 将图片转为Base64编码
        val base64Image = bitmapToBase64(bitmap)
        if (base64Image.isEmpty()) {
            return "图片编码失败"
        }


        // 生成签名（使用原始base64Image）
        val sign = generateSign(appId, base64Image, salt, secretKey)
        Log.d("BaiduImageTranslation", "Sign: $sign")

        // 构建POST请求参数（不进行URL编码，由OkHttp处理）
        val formBody = FormBody.Builder()
            .add("q", base64Image)
            .add("from", from)
            .add("to", to)
            .add("appid", appId)
            .add("salt", salt)
            .add("sign", sign)
            .build()

        // 发送POST请求
        val request = Request.Builder()
            .url("https://fanyi-api.baidu.com/api/trans/vip/translate")
            .post(formBody)
            .build()

        return try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: "No error body"
                    Log.e("BaiduImageTranslation", "HTTP错误: ${response.code}, 响应体: $errorBody")
                    throw IOException("翻译请求失败: HTTP ${response.code} - $errorBody")
                }

                val responseBody = response.body?.string()
                    ?: throw IOException("响应体为空")
                Log.d("BaiduImageTranslation", "完整响应: $responseBody")
                parseTranslationResponse(responseBody)
            }
        } catch (e: Exception) {
            Log.e("BaiduImageTranslation", "翻译出错: ${e.message}")
            "翻译失败: ${e.message ?: "未知错误"}"
        }
    }

    /**
     * 将Bitmap转为Base64字符串
     */
    private fun bitmapToBase64(bitmap: Bitmap): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            // 压缩质量（图片越大，Base64字符串越长，建议压缩至50%质量）
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            Log.d("BaiduImageTranslation", "Base64 length: ${bytes.size}")
            Base64.getEncoder().encodeToString(bytes)
        } catch (e: Exception) {
            Log.e("BaiduImageTranslation", "Base64编码失败", e)
            ""
        }
    }

    /**
     * 解析翻译响应（与文本翻译相同）
     */
    private fun parseTranslationResponse(jsonResponse: String): String {
        return try {
            val jsonObject = JSONObject(jsonResponse)
            if (!jsonObject.has("trans_result")) {
                throw IOException("响应中缺少trans_result字段")
            }

            val transResultArray = jsonObject.getJSONArray("trans_result")
            if (transResultArray.length() == 0) {
                throw IOException("翻译结果为空")
            }

            val firstResult = transResultArray.getJSONObject(0)
            firstResult.getString("dst")
        } catch (e: Exception) {
            Log.e("BaiduImageTranslation", "解析响应失败", e)
            "解析失败: ${e.message ?: "未知错误"}"
        }
    }
}