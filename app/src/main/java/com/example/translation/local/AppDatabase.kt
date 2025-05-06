package com.example.translation.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.translation.api.model.TranslationRecord
import com.example.translation.local.dao.TranslationDao

@Database(
    entities = [TranslationRecord::class],  // 实体类来自 local.entity 包
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao  // DAO 接口

    companion object {
        @Volatile // 确保多线程可见性
        private var INSTANCE: AppDatabase? = null

        // 单例模式获取数据库实例
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // 线程安全双重校验锁
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "translation_database" // 数据库名称
                ).build()
                INSTANCE = instance
                instance // 返回新创建的实例
            }
        }
    }
}