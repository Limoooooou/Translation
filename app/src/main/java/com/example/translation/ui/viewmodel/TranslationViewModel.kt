package com.example.translation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translation.domain.repository.TranslationRepository // 确保路径正确
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val repo: TranslationRepository
) : ViewModel() {

    // 使用委托管理状态
    var inputText by mutableStateOf("")
        private set // 限制外部直接修改

    var resultText by mutableStateOf("")
        private set

    fun translateText() {
        viewModelScope.launch { // 正确使用内置作用域
            resultText = try {
                repo.translate(inputText)
            } catch (e: Exception) {
                "翻译失败: ${e.message}"
            }
        }
    }
}