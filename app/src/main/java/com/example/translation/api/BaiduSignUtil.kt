package com.example.translation.api

import java.security.MessageDigest

object BaiduSignUtil {
    fun generateSign(appId: String, q: String, salt: String, secretKey: String): String {
        // 按照百度api要求拼接字符串
        val signStr = appId + q +salt + secretKey

        // 计算MD5的值
        return md5(signStr)
    }

    public fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(input.toByteArray())
        val digest = md.digest()

        // 将字节数组转换为十六进制字符串
        val hexString = StringBuilder()
        for (byte in digest) {
            val hex = Integer.toHexString( 0xFF and byte.toInt())
            if (hex.length == 1){
                hexString.append('0')
            }
            hexString.append(hex)
        }

        return hexString.toString()
    }
}