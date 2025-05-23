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

@Composable
fun TextTranslationScreen() { // 统一命名
    var inputText by remember { mutableStateOf("hello world!") } // 修正状态初始化
    var outputText by remember { mutableStateOf("你好 世界！") }
    var inputLanguage by remember { mutableStateOf("英文") }
    var outputLanguage by remember { mutableStateOf("简体中文") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // 修正为数字1
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题
        Text(
            text = "Translate",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 输入语言标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "输入", style = MaterialTheme.typography.bodyLarge) // 修正拼写
            Text(text = inputLanguage, style = MaterialTheme.typography.bodyLarge)
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

        // 输出语言标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "输出", style = MaterialTheme.typography.bodyLarge)
            Text(text = outputLanguage, style = MaterialTheme.typography.bodyLarge)
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

        // 搜索按钮
        Button(
            onClick = { /* 处理翻译逻辑 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "search")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextTranslationScreenPreview() {
    MaterialTheme {
        TextTranslationScreen() // 调用修正后的函数
    }
}