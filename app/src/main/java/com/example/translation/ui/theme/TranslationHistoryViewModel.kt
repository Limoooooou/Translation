package com.example.translation.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translation.api.model.TranslationRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslationHistoryViewModel : ViewModel() {
    private val _translationHistory = MutableStateFlow<List<TranslationRecord>>(emptyList())
    val translationHistory: StateFlow<List<TranslationRecord>> = _translationHistory.asStateFlow()

    fun addRecord(record: TranslationRecord) {
        viewModelScope.launch {
            Log.d("ViewModel", "Before emit: ${_translationHistory.value.size}")
            _translationHistory.emit(_translationHistory.value + record)
            Log.d("ViewModel", "After emit: ${_translationHistory.value.size}")
        }
    }

    // 删除单个记录
    fun deleteRecord(record: TranslationRecord) {
        viewModelScope.launch {
            _translationHistory.emit(_translationHistory.value.filter { it != record })
        }
    }

    // 清空所有记录
    fun clearAllRecords() {
        viewModelScope.launch {
            _translationHistory.emit(emptyList())
        }
    }
}
