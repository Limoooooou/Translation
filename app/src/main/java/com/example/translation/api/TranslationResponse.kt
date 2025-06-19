package com.example.translation.api

data class TranslationResponse(
    val from: String,
    val to: String,
    val transResult: List<TransResultItem>
)

data class TransResultItem(
    val src: String,
    val dst: String
)
