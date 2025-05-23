package com.example.translation.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceManager @Inject constructor(
    private val context: Context,
    private val tts: TextToSpeech
) {
    private var isInitialized = false
    private var defaultLanguage = Locale.ENGLISH

    init {
        initializeTts()
    }

    private fun initializeTts() {
        // 新版 Android 中直接使用构造函数传入的已初始化的 tts 对象
        // 不再需要单独设置监听器
        isInitialized = true
        setLanguage(defaultLanguage)
        Log.d("VoiceManager", "TTS initialized")
    }

    fun speak(text: String) {
        if (!isInitialized) {
            Log.w("VoiceManager", "TTS not initialized yet")
            return
        }

        if (text.isBlank()) {
            Log.w("VoiceManager", "Empty text provided for speech")
            return
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        if (isInitialized) {
            tts.stop()
        }
    }

    fun setLanguage(locale: Locale): Boolean {
        return if (isInitialized) {
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("VoiceManager", "Language not supported: ${locale.displayLanguage}")
                false
            } else {
                true
            }
        } else {
            false
        }
    }

    fun shutdown() {
        if (isInitialized) {
            tts.shutdown()
            isInitialized = false
        }
    }
}