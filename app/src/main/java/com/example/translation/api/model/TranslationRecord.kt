package com.example.translation.api.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TranslationRecord(
    val userId: String,
    val sourceText: String,
    val sourceLanguage: String,
    val targetText: String,
    val targetLanguage: String,
    val timestamp: LocalDateTime
) {
    val formattedTime: String
        get() = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
