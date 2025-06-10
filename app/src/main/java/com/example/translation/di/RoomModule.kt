package com.example.translation.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.translation.local.AppDatabase
import com.example.translation.local.dao.TranslationDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    // 提供 Room 数据库实例
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    // 通过数据库实例，提供 DAO
    @Provides
    @Singleton
    fun provideTranslationDao(database: AppDatabase): TranslationDao {
        return database.translationDao()
    }
}