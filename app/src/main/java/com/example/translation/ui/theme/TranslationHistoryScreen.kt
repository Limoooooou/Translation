package com.example.translation.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.translation.api.model.TranslationRecord
import com.example.translation.R

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
) {
    val historyViewModel = remember { TranslationHistoryViewModel() }
    val history = historyViewModel.translationHistory.collectAsState().value
    LaunchedEffect(history) {
        Log.d("HistoryScreen", "History updated: ${history.size}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.translation_history)) },
                actions = {
                    IconButton(
                        onClick = { historyViewModel.clearAllRecords() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.clear_history)
                        )
                    }
                },
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
                TranslationHistoryList(
                    history = history,
                    historyViewModel = historyViewModel
                )
            }
        }
    }
}

@Composable
fun TranslationHistoryList(
    history: List<TranslationRecord>,
    historyViewModel: TranslationHistoryViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(history) { record ->
            TranslationHistoryItem(
                record = record,
                onDelete = { historyViewModel.deleteRecord(it) }
            )
        }
    }
}

@Composable
fun TranslationHistoryItem(
    record: TranslationRecord,
    onDelete: (TranslationRecord) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${record.sourceLanguage}: ${record.sourceText}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${record.targetLanguage}: ${record.targetText}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            IconButton(
                onClick = { onDelete(record) },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.deleteRecord),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationHistoryScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController() // 添加导航控制器预览
    val historyViewModel = remember { TranslationHistoryViewModel() }

    val sampleHistory = listOf(
        TranslationRecord(
            sourceText = "Hello!",
            sourceLanguage = "英文",
            targetText = "你好！",
            targetLanguage = "中文"
        ),
        TranslationRecord(
            sourceText = "Hello World!",
            sourceLanguage = "英文",
            targetText = "你好 世界！",
            targetLanguage = "中文"
        )
    )
    TranslationHistoryScreen(navController = navController)
}