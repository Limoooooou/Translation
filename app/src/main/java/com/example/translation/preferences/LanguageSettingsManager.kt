package com.example.translation.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 定义 DataStore 名称的扩展属性（推荐方式）
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class LanguageSettingsManager(private val context: Context) {
    private val dataStore = context.dataStore // 通过扩展属性获取实例

    private object PrefsKeys {
        val SOURCE_LANG = stringPreferencesKey("source_language")
        val TARGET_LANG = stringPreferencesKey("target_language")
    }

    suspend fun setSourceLanguage(lang: String) {
        dataStore.edit { settings ->
            settings[PrefsKeys.SOURCE_LANG] = lang
        }
    }

    val targetLanguage: Flow<String> = dataStore.data
        .map { prefs -> prefs[PrefsKeys.TARGET_LANG] ?: "en" }
}