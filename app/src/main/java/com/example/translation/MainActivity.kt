package com.example.translation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.translation.navigation.MainNavigation
import com.example.translation.ui.theme.TranslationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TranslationTheme {
                MainNavigation()
            }
        }
    }
}
//lcx
//whx
//win