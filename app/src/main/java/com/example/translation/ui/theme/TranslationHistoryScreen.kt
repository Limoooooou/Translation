package com.example.translation.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.translation.R
import com.example.translation.navigation.NavigationBar

data class TranslationRecord(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationHistoryScreen(
    navController: NavHostController, // 添加导航控制器参数
    history: List<TranslationRecord> = emptyList()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.translation_history)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (history.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_history),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                TranslationHistoryList(history = history)
            }
        }
    }
}

@Composable
fun TranslationHistoryList(history: List<TranslationRecord>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(history) { record ->
            TranslationHistoryItem(record = record)
        }
    }
}

@Composable
fun TranslationHistoryItem(record: TranslationRecord) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), // 修复原代码中 fillMaxSize() 的问题（应使用 fillMaxWidth()）
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${record.sourceLanguage}: ${record.originalText}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${record.targetLanguage}: ${record.translatedText}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationHistoryScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController() // 添加导航控制器预览
    val sampleHistory = listOf(
        TranslationRecord("Hello!", "你好！", "英文", "简体中文"),
        TranslationRecord("Hello World!", "你好 世界！", "英文", "简体中文")
    )
    TranslationHistoryScreen(navController = navController, history = sampleHistory)
}