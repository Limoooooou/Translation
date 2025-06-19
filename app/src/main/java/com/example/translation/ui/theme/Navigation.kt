package com.example.translation.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.translation.ui.theme.TranslationHistoryScreen
import com.example.translation.ui.theme.VoiceTranslationScreen
import com.example.translation.ui.theme.CameraTranslationScreen
import com.example.translation.ui.theme.TextTranslationScreen
import com.example.translation.ui.theme.TranslationResultScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object TextTranslationScreen : Screen("textScreen", "文本翻译", Icons.Default.Keyboard)
    object CameraTranslationScreen : Screen("cameraScreen", "拍照翻译", Icons.Default.PhotoCamera)
    object VoiceTranslationScreen : Screen("voiceScreen", "语音翻译", Icons.Default.Mic)
    object HistoryScreen : Screen("historyScreen", "翻译历史", Icons.Default.History)
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { NavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TextTranslationScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.TextTranslationScreen.route) {
                TextTranslationScreen(
                    navController = navController
                )
            }
            composable(Screen.CameraTranslationScreen.route) {
                CameraTranslationScreen(navController)
            }
            composable(Screen.VoiceTranslationScreen.route) {
                VoiceTranslationScreen(navController)
            }
            composable(Screen.HistoryScreen.route) {
                TranslationHistoryScreen(navController)
            }
            composable(
                "translationResultScreen?result={result}",
                arguments = listOf(
                    navArgument("result") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val result = backStackEntry.arguments?.getString("result") ?: "翻译结果为空"
                Log.d("TranslationResult", "Received result: $result")
                TranslationResultScreen(navController, result)
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.TextTranslationScreen,
        Screen.CameraTranslationScreen,
        Screen.VoiceTranslationScreen,
        Screen.HistoryScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    androidx.compose.material3.NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route){
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}