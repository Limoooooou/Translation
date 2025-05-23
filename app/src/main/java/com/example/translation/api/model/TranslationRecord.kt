package com.example.translation.api.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceText: String,
    val sourceLanguage: String,
    val targetText: String,
    val targetLanguage: String,
    @ColumnInfo(name = "created_at")
    val timestamp: Long = System.currentTimeMillis() // 改用时间戳更易存储
)
