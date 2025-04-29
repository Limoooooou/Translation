package com.example.translation.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import javax.inject.Inject

class VoiceManager @Inject constructor(
    private val context: Context,
    private val tts: TextToSpeech
) {
    // 语音处理逻辑...
}