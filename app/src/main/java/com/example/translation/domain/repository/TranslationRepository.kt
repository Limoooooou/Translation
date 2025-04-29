package com.example.translation.domain.repository

interface TranslationRepository {
    suspend fun translate(text: String): String
}