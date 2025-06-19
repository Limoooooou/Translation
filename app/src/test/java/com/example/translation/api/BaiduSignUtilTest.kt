package com.example.translation.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BaiduSignUtilTest {

    @Test
    fun testGenerateSign() {
        val appId = "testAppId"
        val q = "testText"
        val salt = "testSalt"
        val secretKey = "testSecretKey"

        val expectedSign = BaiduSignUtil.md5("$appId$q$salt$secretKey")
        val actualSign = BaiduSignUtil.generateSign(appId, q, salt, secretKey)

        Assertions.assertEquals(expectedSign, actualSign)
    }
}