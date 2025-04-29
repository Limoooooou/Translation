package com.example.translation.di

import com.example.translation.domain.repository.TranslationRepositoryImpl
import com.example.translation.domain.repository.TranslationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindTranslationRepository(
        impl: TranslationRepositoryImpl // 具体实现类
    ): TranslationRepository
}