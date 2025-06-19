package com.example.translation.ui.theme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.translation.api.BaiduImageTranslationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTranslationScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var sourceLanguage by remember { mutableStateOf("auto") }
    var targetLanguage by remember { mutableStateOf("zh") }
    var translationResult by remember { mutableStateOf("") }
    var showSourceLanguageDialog by remember { mutableStateOf(false) }
    var showTargetLanguageDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // 相机启动器
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            capturedImage = imageBitmap
        }
    }

    // 权限启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }
    }

    // 可用语言列表
    val languages = listOf("自动检测", "中文", "英语", "日语", "法语", "西班牙语", "俄语")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照翻译") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (capturedImage == null) {
                // 未拍摄照片时显示拍照按钮
                Button(
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                cameraLauncher.launch(intent)
                            }
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as ComponentActivity,
                                Manifest.permission.CAMERA
                            ) -> {}
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "拍照")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("拍摄照片")
                    }
                }
            } else {
                // 已拍摄照片，显示照片和翻译选项
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 显示拍摄的照片
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4/3f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        val image = capturedImage // 关键修改：使用临时变量保存状态值
                        if (image != null) {
                            Image(
                                bitmap = image.asImageBitmap(),
                                contentDescription = "拍摄的照片",
                                modifier = Modifier.fillMaxSize(),
                                alignment = Alignment.Center
                            )
                        } else {
                            Text("加载中...")
                        }
                    }

                    // 源语言选择行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "源语言", style = MaterialTheme.typography.bodyLarge)

                        OutlinedButton(
                            onClick = { showSourceLanguageDialog = true },
                            modifier = Modifier.width(120.dp)
                        ) {
                            Text(
                                text = when (sourceLanguage) {
                                    "auto" -> "自动检测"
                                    "zh" -> "中文"
                                    "en" -> "英语"
                                    "ja" -> "日语"
                                    "fr" -> "法语"
                                    "es" -> "西班牙语"
                                    "ru" -> "俄语"
                                    else -> sourceLanguage
                                }
                            )
                        }
                    }

                    // 目标语言选择行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "目标语言", style = MaterialTheme.typography.bodyLarge)

                        OutlinedButton(
                            onClick = { showTargetLanguageDialog = true },
                            modifier = Modifier.width(120.dp)
                        ) {
                            Text(
                                text = when (targetLanguage) {
                                    "zh" -> "中文"
                                    "en" -> "英语"
                                    "ja" -> "日语"
                                    "fr" -> "法语"
                                    "es" -> "西班牙语"
                                    "ru" -> "俄语"
                                    else -> targetLanguage
                                }
                            )
                        }
                    }

                    // 翻译按钮
                    Button(
                        onClick = {
                            if (capturedImage != null) {
                                coroutineScope.launch {
                                    isLoading = true

                                    // 调用图片翻译API
                                    val result = try {
                                        BaiduImageTranslationApi.translateImage(
                                            bitmap = capturedImage!!,
                                            from = sourceLanguage,
                                            to = targetLanguage
                                        )
                                    } catch (e: Exception) {
                                        "翻译错误: ${e.message ?: "未知错误"}"
                                    }
                                    Log.d("CameraTranslation", "Translation result: $result")

                                    // 保存结果并导航到结果页
                                    withContext(Dispatchers.Main) {
                                        navController.navigate("translationResultScreen?result=$result")
                                    }
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("开始翻译")
                        }
                    }
                }
            }
        }
    }

    // 源语言选择对话框
    if (showSourceLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showSourceLanguageDialog = false },
            title = { Text("选择源语言") },
            text = {
                Column {
                    languages.forEach { language ->
                        Button(
                            onClick = {
                                val langCode = when (language) {
                                    "自动检测" -> "auto"
                                    "中文" -> "zh"
                                    "英语" -> "en"
                                    "日语" -> "ja"
                                    "法语" -> "fr"
                                    "西班牙语" -> "es"
                                    "俄语" -> "ru"
                                    else -> "auto"
                                }
                                sourceLanguage = langCode
                                showSourceLanguageDialog = false
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
                TextButton(onClick = { showSourceLanguageDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 目标语言选择对话框
    if (showTargetLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showTargetLanguageDialog = false },
            title = { Text("选择目标语言") },
            text = {
                Column {
                    languages.filter { it != "自动检测" }.forEach { language ->
                        Button(
                            onClick = {
                                val langCode = when (language) {
                                    "中文" -> "zh"
                                    "英语" -> "en"
                                    "日语" -> "ja"
                                    "法语" -> "fr"
                                    "西班牙语" -> "es"
                                    "俄语" -> "ru"
                                    else -> "en"
                                }
                                targetLanguage = langCode
                                showTargetLanguageDialog = false
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
                TextButton(onClick = { showTargetLanguageDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCameraTranslationScreen() {
    val navController = rememberNavController()
    TranslationTheme {
        CameraTranslationScreen(navController)
    }
}