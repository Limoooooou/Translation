package com.example.translation.util

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.MessageDigest

object BaiduSignUtil {
    /**
     * 生成百度API要求的MD5签名
     * @param appId 百度分配的APP ID
     * @param text 待翻译文本
     * @param salt 随机数
     * @param secretKey 百度分配的密钥
     */
    fun generateSign(
        appId: String,
        text: String,
        salt: String = generateRandomSalt(),
        secretKey: String
    ): String {
        // 1. 对文本进行URL编码（百度API要求q参数必须URL编码）
        val encodedText = urlEncode(text)

        // 2. 构建签名原始字符串：appid + 编码文本 + 盐 + secretKey
        val rawSign = "$appId$encodedText$salt$secretKey"

        // 3. 生成MD5签名（小写）
        return md5(rawSign)
    }

    private fun generateRandomSalt(): String {
        return (100000..999999).random().toString()
    }

    private fun md5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray(charset("UTF-8")))
            digest.fold("", { str, byte -> str + "%02x".format(byte) })
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun urlEncode(text: String): String {
        return try {
            URLEncoder.encode(text, "UTF-8")
                .replace("+", "%20") // 百度API要求将+替换为%20
                .replace("*", "%2A")
                .replace("%7E", "~")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            text
        }
    }
}