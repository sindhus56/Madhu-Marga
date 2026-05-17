package com.example.madhumargaagriculture

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel

class ThemeViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("settings", 0)

    var isDark = mutableStateOf(
        prefs.getBoolean("dark_mode", false)
    )
        private set

    fun toggleTheme() {
        isDark.value = !isDark.value
        prefs.edit { putBoolean("dark_mode", isDark.value) }
    }
}
