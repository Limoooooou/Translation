package com.example.translation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.translation.ui.viewmodel.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationScreen(
    viewModel: TranslationViewModel,
    modifier: Modifier = Modifier
) {
    val inputText by viewModel.inputText
    val resultText by viewModel.resultText
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 输入框：绑定 ViewModel 的 inputText，并通过 updateInput 方法更新
        OutlinedTextField(
            value = inputText,
            onValueChange = { viewModel.updateInput(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("输入要翻译的文本") },
            trailingIcon = {
                if (inputText.isNotEmpty()) { // 使用 inputText 替代 uiState.inputText
                    IconButton(onClick = { viewModel.updateInput("") }) { // 调用 updateInput 清空
                        Icon(Icons.Default.Clear, "清空")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.translate(inputText, "en", "zh") // 直接调用 translate 方法（需传入语言参数）
                }
            ),
            maxLines = 5
        )

        // 翻译按钮：触发 translate 方法，使用独立状态控制禁用
        Button(
            onClick = { viewModel.translate(inputText, "en", "zh") }, // 示例语言代码，需从界面获取实际语言
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = inputText.isNotEmpty() && !isLoading
        ) {
            if (isLoading) { // 使用 isLoading 替代 uiState.isLoading
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("翻译")
            }
        }

        // 错误信息：使用独立状态 errorMessage
        errorMessage?.let { message -> // 替代 uiState.errorMessage
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 翻译结果：使用独立状态 resultText
        if (resultText.isNotEmpty()) { // 替代 uiState.resultText
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "翻译结果",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultText, // 直接使用 resultText
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}