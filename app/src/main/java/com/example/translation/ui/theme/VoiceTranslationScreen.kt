package com.example.translation.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.translation.util.AudioRecorderUtil
import kotlinx.coroutines.CoroutineScope
import com.example.translation.api.BaiduVoiceRecognitionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceTranslationScreen(
    navController: NavHostController
) {
    var inputLanguage by remember { mutableStateOf("简体中文") }
    var outputLanguage by remember { mutableStateOf("英文") }
    var originalText by remember { mutableStateOf("等待说话...") }
    var translatedText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val hasRecordAudioPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    // 注册权限请求回调
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "需要录音权限才能使用语音翻译",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
    // 开始录音
    fun startRecording() {
        val error = AudioRecorderUtil.startRecording()
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    // 停止录音
    fun stopRecording() {
        val audioFile = AudioRecorderUtil.stopRecording()
        if (audioFile != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val (srcText, dstText) = BaiduVoiceRecognitionApi.translateVoice(
                    audioFile,
                    inputLanguage,
                    outputLanguage
                )
                withContext(Dispatchers.Main) {
                    originalText = srcText
                    translatedText = dstText
                }
            }
        }
    }
    // 检查并请求权限的函数
    fun checkAndRequestAudioPermission() {
        if (!hasRecordAudioPermission) {
            recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        } else {
            // 权限已授予，执行录音操作
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
            isRecording = !isRecording
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("语音翻译") },
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
                    onClick = { checkAndRequestAudioPermission() },
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
