package com.example.translation.di

import android.content.Context
import androidx.room.Room
import com.example.translation.domain.repository.TranslationRepository
import com.example.translation.api.service.TextTranslationService
import com.example.translation.domain.repository.RealTranslationRepository
import com.example.translation.domain.repository.TranslationRepositoryImpl
import com.example.translation.local.dao.TranslationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.fanyi.baidu.com/api/trans/vip/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named("appId")
    fun provideAppId(): String {
        return "20250609002377601"
    }

    @Provides
    @Singleton
    @Named("secretKey")
    fun provideSecretKey(): String {
        return "bRZ7m1bV5nztVBsQdhF2"
    }

    @Provides
    @Singleton
    fun provideTextTranslationService(retrofit: Retrofit): TextTranslationService {
        return retrofit.create(TextTranslationService::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslationRepository(
        service: TextTranslationService,
        @Named("appId")appId: String,
        @Named("secretKey")secretKey: String,
        translationDao: TranslationDao
    ): TranslationRepository {
        return RealTranslationRepository(translationDao, service, appId, secretKey)
    }
}
