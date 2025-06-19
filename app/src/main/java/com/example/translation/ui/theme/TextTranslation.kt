package com.example.translation.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTranslationScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val historyViewModel = remember { TranslationHistoryViewModel() }

    var sourceLanguageIndex by remember { mutableIntStateOf(0) } // 默认选择中文
    var targetLanguageIndex by remember { mutableIntStateOf(1) } // 默认选择英文
    var inputText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }
    var isPlayingSource by remember { mutableStateOf(false) }
    var isPlayingTarget by remember { mutableStateOf(false) }
    var copySuccess by remember { mutableStateOf(false) }

    val supportedLanguages = listOf("中文", "英文", "日文", "韩文", "德语")

    // 获取系统服务
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val languageCodeMap = mapOf(
        "中文" to "zh",
        "英文" to "en",
        "日文" to "jp",
        "韩文" to "kor",
        "德语" to "de"
    )

    /**
     * 百度云通用文本翻译
     */
    fun generalTextTranslation(
        coroutineScope: CoroutineScope,
        text: String,
        sourceLanguageText: String = "auto",
        targetLanguageText: String = "英文",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // 语言代码列表
                val languageCodeMap = mapOf(
                    "中文" to "zh",
                    "英文" to "en",
                    "日文" to "jp",
                    "韩文" to "kor",
                    "德语" to "de"
                )

                val sourceLanguageCode = languageCodeMap[sourceLanguageText] ?: "auto"
                val targetLanguageCode = languageCodeMap[targetLanguageText] ?: "en"

                // 调用翻译API
                val result = BaiduTranslationApi.translateText(text, sourceLanguageCode, targetLanguageCode)

                withContext(Dispatchers.Main) {
                    onSuccess(result)

                    // 保存翻译记录
                    val record = com.example.translation.api.model.TranslationRecord(
                        sourceText = text,
                        sourceLanguage = sourceLanguageText,
                        targetText = result,
                        targetLanguage = targetLanguageText
                    )
                    historyViewModel.addRecord(record)
                    Log.d("Translation", "Record saved: ${record.sourceText} -> ${record.targetText}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "未知错误")
                    Log.e("Translation", "Save record error: ${e.message}")
                }
            }
        }
    }

    fun speakText(text: String, language: String) {
        if (text.isBlank()) return

        // 从映射表获取语言代码字符串
        val languageCode = languageCodeMap[language] ?: "en"

        // 将语言代码字符串转换为Locale
        val locale = Locale.forLanguageTag(languageCode)
    }

    /**
     * 复制文本到剪贴板
     */
    fun copyToClipboard(text: String) {
        if (text.isBlank()) return

        clipboardManager.setText(AnnotatedString(text))
        copySuccess = true

        // 3秒后隐藏复制成功提示
        CoroutineScope(Dispatchers.Main).launch {
            kotlinx.coroutines.delay(3000)
            copySuccess = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文本翻译") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 源语言选择
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "源语言:", modifier = Modifier.padding(bottom = 4.dp))
                LanguageDropdownSelector(
                    selectedLanguage = supportedLanguages[sourceLanguageIndex],
                    onLanguageSelected = { index -> sourceLanguageIndex = index },
                    allLanguages = supportedLanguages
                )
            }

            // 输入文本区域
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "请输入要翻译的内容:", modifier = Modifier.padding(bottom = 4.dp))

                // 文本输入框和功能按钮
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.small
                        )
                ) {
                    Column {
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("请输入文本...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        // 功能按钮：语音播放和复制
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    speakText(
                                        inputText,
                                        supportedLanguages[sourceLanguageIndex]
                                    )
                                },
                                enabled = inputText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.VolumeUp,
                                    contentDescription = "播放",
                                    tint = if (isPlayingSource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { copyToClipboard(inputText) },
                                enabled = inputText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.ContentCopy,
                                    contentDescription = "复制",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 目标语言选择
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "目标翻译的语言:", modifier = Modifier.padding(bottom = 4.dp))
                LanguageDropdownSelector(
                    selectedLanguage = supportedLanguages[targetLanguageIndex],
                    onLanguageSelected = { index -> targetLanguageIndex = index },
                    allLanguages = supportedLanguages
                )
            }

            // 翻译结果区域
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "翻译结果:", modifier = Modifier.padding(bottom = 4.dp))

                // 结果显示框和功能按钮
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.small
                        )
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (translatedText.isEmpty()) {
                                Text(
                                    text = "翻译结果将显示在这里",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            } else {
                                Text(
                                    text = translatedText,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // 功能按钮：语音播放和复制
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    speakText(
                                        translatedText,
                                        supportedLanguages[targetLanguageIndex]
                                    )
                                },
                                enabled = translatedText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.VolumeUp,
                                    contentDescription = "播放",
                                    tint = if (isPlayingTarget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { copyToClipboard(translatedText) },
                                enabled = translatedText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.ContentCopy,
                                    contentDescription = "复制",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 复制成功提示
            if (copySuccess) {
                Text(
                    text = "已复制到剪贴板",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp
                )
            }

            // 翻译按钮
            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    if (inputText.isNotEmpty() && !isTranslating) {
                        isTranslating = true
                        generalTextTranslation(
                            coroutineScope = coroutineScope,
                            text = inputText,
                            sourceLanguageText = supportedLanguages[sourceLanguageIndex],
                            targetLanguageText = supportedLanguages[targetLanguageIndex],
                            onSuccess = { result ->
                                translatedText = result
                                isTranslating = false
                            },
                            onError = { error ->
                                translatedText = "翻译出错: $error"
                                isTranslating = false
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isTranslating && inputText.isNotEmpty()
            ) {
                if (isTranslating) {
                    Text("翻译中...", fontSize = 18.sp)
                } else {
                    Text("翻译", fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdownSelector(
    selectedLanguage: String,
    onLanguageSelected: (Int) -> Unit,
    allLanguages: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedLanguage,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "展开语言选择"
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            allLanguages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = { Text(text = language) },
                    onClick = {
                        onLanguageSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTextTranslationScreen() {
    val navController = rememberNavController()
    TranslationTheme {
        TextTranslationScreen(navController)
    }
}