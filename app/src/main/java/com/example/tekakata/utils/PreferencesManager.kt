package com.example.tekakata.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "tekakata_prefs"
        private const val KEY_HIGHEST_LEVEL = "HIGHEST_LEVEL"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getHighestLevel(): Int {
        return prefs.getInt(KEY_HIGHEST_LEVEL, 1)
    }

    fun saveHighestLevel(level: Int) {
        val current = getHighestLevel()
        if (level > current) {
            prefs.edit().putInt(KEY_HIGHEST_LEVEL, level).apply()
        }
    }
}
