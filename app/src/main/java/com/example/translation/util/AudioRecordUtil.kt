package com.example.translation.util

import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.IOException
import java.util.UUID

object AudioRecorderUtil {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var isRecording = false

    // 开始录音
    fun startRecording(): String? {
        if (isRecording) return "正在录音中"

        try {
            audioFile = createAudioFile()
            if (audioFile == null) return "创建录音文件失败"

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return "录音准备失败: ${e.message}"
        }
    }

    // 停止录音
    fun stopRecording(): File? {
        if (!isRecording) return null

        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            return audioFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // 取消录音
    fun cancelRecording() {
        if (isRecording) {
            mediaRecorder?.release()
            mediaRecorder = null
            audioFile?.delete()
            isRecording = false
        }
    }

    // 创建录音文件
    private fun createAudioFile(): File? {
        val audioDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC
        ).apply { mkdirs() }
        val fileName = "voice_${UUID.randomUUID()}.mp4"
        return File(audioDir, fileName)
    }
}