package com.example.translation.local.dao

import androidx.room.*
import com.example.translation.api.model.TranslationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Insert
    fun insertRecord(record: TranslationRecord): Long

    @Query("SELECT * FROM translation_history ORDER BY created_at DESC")
    fun getAllHistory(): Flow<List<TranslationRecord>>
}