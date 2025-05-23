package com.example.translation.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate

@Composable
fun TextTranslationScreen(
    onNavigate: (String) -> Unit = {} // 导航回调
) {
    // 状态管理
    var inputText by remember { mutableStateOf("hello world!") }
    var outputText by remember { mutableStateOf("你好 世界！") }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguagePair by remember {
        mutableStateOf("英文" to "简体中文")
    }

    // 可用语言列表
    val languages = listOf("英文", "简体中文", "日语", "法语", "西班牙语")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 主内容区域
        Column {
            // 标题
            Text(
                text = "Translate",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 输入语言行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "输入", style = MaterialTheme.typography.bodyLarge)

                // 语言选择按钮
                OutlinedButton(
                    onClick = { showLanguageDialog = true },
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(currentLanguagePair.first)
                }
            }

            // 输入文本框
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(16.dp),
                textStyle = TextStyle(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 输出语言行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "输出", style = MaterialTheme.typography.bodyLarge)

                // 语言选择按钮
                OutlinedButton(
                    onClick = { showLanguageDialog = true },
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(currentLanguagePair.second)
                }
            }

            // 输出文本框
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(16.dp)
            ) {
                Text(text = outputText, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 翻译按钮
            Button(
                onClick = {
                    // 这里调用翻译API
                    outputText = translateText(inputText,
                        currentLanguagePair.first,
                        currentLanguagePair.second)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Translate", style = MaterialTheme.typography.labelLarge)
            }
        }

        // 底部导航栏
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            NavigationBarItem(
                selected = true,
                onClick = { onNavigate("translate") },
                icon = { Icon(Icons.Filled.Translate, contentDescription = "翻译") },
                label = { Text("翻译") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { onNavigate("history") },
                icon = { Icon(Icons.Filled.History, contentDescription = "历史") },
                label = { Text("历史") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { onNavigate("settings") },
                icon = { Icon(Icons.Filled.Settings, contentDescription = "设置") },
                label = { Text("设置") }
            )
        }
    }

    // 语言选择对话框
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("选择语言") },
            text = {
                Column {
                    languages.forEach { language ->
                        Button(
                            onClick = {
                                currentLanguagePair = language to currentLanguagePair.second
                                showLanguageDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(language)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

// 模拟翻译函数
fun translateText(text: String, fromLang: String, toLang: String): String {
    // 实际项目中这里调用翻译API
    return when {
        fromLang == "英文" && toLang == "简体中文" -> "这是翻译结果"
        else -> "Translated: $text"
    }
}

@Preview(showBackground = true)
@Composable
fun TextTranslationScreenPreview() {
    MaterialTheme {
        TextTranslationScreen()
    }
}