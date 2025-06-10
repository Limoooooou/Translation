package com.example.translation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translation.domain.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(
    val repository: TranslationRepository
) : ViewModel() {

    private var _inputText = mutableStateOf("")
    val inputText: State<String> get() = _inputText // 暴露为 State<String>

    private var _resultText = mutableStateOf("")
    val resultText: State<String> get() = _resultText // 暴露为 State<String>

    private var _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading // 暴露为 State<Boolean>

    private var _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage // 暴露为 State<String?>

    // 更新状态的方法
    fun updateInput(text: String) {
        _inputText.value = text // 通过.value修改内部状态
        _errorMessage.value = null
    }

    fun translate(text: String, sourceLang: String, targetLang: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _resultText.value = repository.translateText(text, sourceLang, targetLang)
            } catch (e: Exception) {
                _errorMessage.value = "翻译失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}