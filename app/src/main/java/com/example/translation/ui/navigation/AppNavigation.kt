package com.example.translation.ui.navigation

import androidx.navigation.NavHostController
import com.example.translation.ui.screens.Screen

class AppNavigation(private val navController: NavHostController) {

    // 定义所有路由
    sealed class Route(val path: String) {
        object Translation : Route("translation")
        object History : Route("history")
        object Settings : Route("settings")
        object About : Route("about")
    }

    // 导航到指定页面
    fun goToPage(page: String) {
        when (page) {
            Screen.Translation.route -> navigateToTranslation()
            Screen.History.route -> navigateToHistory()
            Screen.Settings.route -> navigateToSettings()
            Screen.About.route -> navigateToAbout()
            else -> throw IllegalArgumentException("Unknown route: $page")
        }
    }

    // 具体页面导航方法
    fun navigateToTranslation() {
        navController.navigate(Route.Translation.path) {
            // 清除返回栈中除首页外的所有页面
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }

    fun navigateToHistory() {
        navController.navigate(Route.History.path) {
            launchSingleTop = true
        }
    }

    fun navigateToSettings() {
        navController.navigate(Route.Settings.path) {
            launchSingleTop = true
        }
    }

    fun navigateToAbout() {
        navController.navigate(Route.About.path) {
            launchSingleTop = true
        }
    }

    // 返回上一页
    fun navigateBack() {
        navController.popBackStack()
    }

    // 带参数的导航示例
    fun navigateToTranslationDetail(translationId: String) {
        navController.navigate("translation_detail/$translationId") {
            launchSingleTop = true
        }
    }

    // 获取当前路由
    val currentRoute: String?
        get() = navController.currentDestination?.route
}