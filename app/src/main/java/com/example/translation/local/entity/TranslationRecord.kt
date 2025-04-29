package com.example.translation.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_records")
data class TranslationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val sourceText: String,
    val sourceLanguage: String,
    val targetText: String,
    val targetLanguage: String,
    @ColumnInfo(name = "created_at") val timestamp: Long = System.currentTimeMillis()
)
