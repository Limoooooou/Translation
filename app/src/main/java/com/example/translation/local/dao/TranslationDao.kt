package com.example.translation.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.translation.api.model.TranslationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Insert
    suspend fun saveTranslationRecord(record: TranslationRecord)

    // 修正查询语句中的列名（使用 created_at）
    @Query("SELECT * FROM translation_records WHERE userId = :userId ORDER BY created_at DESC")
    fun getTranslationRecords(userId: String): Flow<List<TranslationRecord>>
}