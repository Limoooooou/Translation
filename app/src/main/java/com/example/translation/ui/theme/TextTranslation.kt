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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.translation.api.model.TranslationRecord
import com.example.translation.domain.repository.TranslationRepository
import com.example.translation.ui.viewmodel.TranslationViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.translation.util.LanguageMapper


@Composable
fun TextTranslationScreen(
    navController: NavHostController,
    viewModel: TranslationViewModel = hiltViewModel()
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguagePair by remember { mutableStateOf("英文" to "简体中文") }

    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val resultText by viewModel.resultText
    val inputText by viewModel.inputText

    val languages = listOf("英文", "简体中文", "日语", "法语", "西班牙语")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Translate", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

            // 输入语言行
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("输入", style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(onClick = { showLanguageDialog = true }, modifier = Modifier.width(100.dp)) {
                    Text(currentLanguagePair.first)
                }
            }

            // 输入文本框：绑定 ViewModel 的 inputText 状态
            BasicTextField(
                value = inputText, // 直接使用 viewModel.inputText
                onValueChange = { viewModel.updateInput(it) }, // 通过 ViewModel 方法更新状态
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.LightGray, MaterialTheme.shapes.small)
                    .padding(16.dp),
                textStyle = TextStyle(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 输出语言行（同输入语言逻辑）
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("输出", style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(onClick = { showLanguageDialog = true }, modifier = Modifier.width(100.dp)) {
                    Text(currentLanguagePair.second)
                }
            }

            // 输出文本框
            Box(Modifier.fillMaxWidth().background(Color.LightGray, MaterialTheme.shapes.small).padding(16.dp)) {
                Text(resultText.ifEmpty { "翻译结果将显示在这里" }, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 翻译按钮：触发 ViewModel 的 translate 方法（已处理协程）
            Button(
                onClick = {
                    val sourceLang = LanguageMapper.getBaiduLanguageCode(currentLanguagePair.first)
                    val targetLang = LanguageMapper.getBaiduLanguageCode(currentLanguagePair.second)

                    viewModel.translate(inputText, sourceLang, targetLang) // 直接调用 ViewModel 方法
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading // 根据 ViewModel 的 isLoading 状态禁用按钮
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Translate", style = MaterialTheme.typography.labelLarge)
                }
            }

            // 错误信息显示
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    // 语言选择对话框（优化为输入/输出语言独立选择）
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("选择语言") },
            text = {
                Column {
                    // 输入语言选择
                    Text("输入语言", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    languages.forEach { lang ->
                        Button(
                            onClick = {
                                currentLanguagePair = lang to currentLanguagePair.second
                                showLanguageDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        ) {
                            Text(lang)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 输出语言选择
                    Text("输出语言", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    languages.forEach { lang ->
                        Button(
                            onClick = {
                                currentLanguagePair = currentLanguagePair.first to lang
                                showLanguageDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        ) {
                            Text(lang)
                        }
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

// 工具函数：语言映射（与 ViewModel 逻辑一致）
fun getLanguageCode(displayLanguage: String): String {
    return when (displayLanguage) {
        "英文" -> "en"
        "简体中文" -> "zh"
        "日语" -> "ja"
        "法语" -> "fr"
        "西班牙语" -> "es"
        else -> "en"
    }
}

@Preview(showBackground = true)
@Composable
fun TextTranslationScreenPreview() {
    val fakeRepository = object : TranslationRepository {
        override suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
            return "预览翻译结果：$text"
        }

        override fun getHistory(): Flow<List<TranslationRecord>> {
            return flowOf(emptyList())
        }
    }
    val navController = rememberNavController()
    MaterialTheme {
        TextTranslationScreen(
            viewModel = TranslationViewModel(fakeRepository), // 注意：实际需通过 Hilt 注入，此处仅为预览模拟
            navController = navController
        )
    }
}