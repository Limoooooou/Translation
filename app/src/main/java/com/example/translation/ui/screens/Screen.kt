package com.example.translation.ui.screens

sealed class Screen(val route: String) {
    object Translation : Screen("translation")
    object History : Screen("history")
    object Settings : Screen("settings")
    object About : Screen("about")

    // 带参数的屏幕示例
    object TranslationDetail : Screen("translation_detail/{id}") {
        fun createRoute(id: String) = "translation_detail/$id"
    }
}