package com.example.translation.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun VoiceTranslationScreen(
    navController: NavHostController
) {
    var inputLanguage by remember { mutableStateOf("简体中文") }
    var outputLanguage by remember { mutableStateOf("英文") }
    var originalText by remember { mutableStateOf("有些事情，任何言语都无法弥补，任何字母组合都无法纠正...") }
    var translatedText by remember { mutableStateOf("There are some things that no words can fix...") }
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 语言选择行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LanguageSelectionDropdown(
                selectedLanguage = inputLanguage,
                onLanguageSelected = { language -> inputLanguage = language },
                label = "输入"
            )

            LanguageSelectionDropdown(
                selectedLanguage = outputLanguage,
                onLanguageSelected = { language -> outputLanguage = language },
                label = "输出"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 原文部分
        Text(
            text = "原文",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = originalText,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 译文部分
        Text(
            text = "译文",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = translatedText,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 录音按钮
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { isRecording = !isRecording },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isRecording) "松开 结束" else "按住 说话",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun LanguageSelectionDropdown(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("简体中文", "英文", "日本語", "한국어", "Español", "Français")

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(selectedLanguage)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Language dropdown")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(180.dp)
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVoiceTranslationScreen() {
    MaterialTheme {
        val navController = rememberNavController()
        VoiceTranslationScreen(navController = navController)
    }
}
