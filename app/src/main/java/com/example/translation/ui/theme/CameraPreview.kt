package com.example.translation.ui.theme


import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onImageCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                    // 示例：通过外部按钮触发拍照
                    // 实际使用时应在外部Composable中调用imageCapture.takePicture()

                } catch (exc: Exception) {
                    Log.e("Camera", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

// 在父Composable中使用：
@Composable
fun CameraScreen() {
    val context = LocalContext.current  // 在Composable作用域内获取context
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column {
        CameraPreview { capturedBitmap ->
            bitmap = capturedBitmap
        }

        Button(onClick = {
            imageCapture.value?.let { capture ->
                val file = File.createTempFile("ocr_", ".jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                capture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),  // 使用预先获取的context
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val bitmap = BitmapFactory.decodeFile(file.path)
                            // 处理识别结果
                        }
                        override fun onError(exc: ImageCaptureException) {
                            Log.e("Camera", "Error capturing image", exc)
                        }
                    }
                )
            }
        }) {
            Text("Capture Image")
        }
    }
}