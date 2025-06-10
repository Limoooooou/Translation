package com.example.translation.util

object LanguageMapper {
    private val languageMap = mapOf(
        "英文" to "en",
        "简体中文" to "zh",
        "日语" to "jp",
        "法语" to "fra",
        "西班牙语" to "spa"
    )

    fun getBaiduLanguageCode(displayName: String): String {
        return languageMap[displayName] ?: "auto"
    }
}