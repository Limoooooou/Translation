package com.example.translateapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translateapp.domain.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val repo: TranslationRepository
) : ViewModel() {

    // 输入文本（使用 mutableStateOf 直接绑定到 Compose UI）
    var inputText by mutableStateOf("")
        private set

    // 翻译结果（使用 mutableStateOf 直接绑定到 Compose UI）
    var resultText by mutableStateOf("")
        private set

    // 加载状态（使用 StateFlow 管理）
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误消息（使用 StateFlow 管理）
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 更新输入文本
    fun updateInput(text: String) {
        inputText = text
    }

    // 调用翻译接口
    fun translateText() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                resultText = repo.translateText(
                    text = inputText,
                    sourceLang = "auto", // 实际应从 LanguageSettingsManager 获取
                    targetLang = "en"    // 实际应从 LanguageSettingsManager 获取
                )
            } catch (e: Exception) {
                _errorMessage.value = "翻译失败: ${e.message}"
                resultText = ""
            } finally {
                _isLoading.value = false
            }
        }
    }
}