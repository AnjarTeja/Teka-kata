package com.example.tekakata.utils

import android.content.Context
import android.content.SharedPreferences

data class LevelProgress(
    val foundWords: Set<String>,
    val hintCount: Int
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "tekakata_prefs"
        private const val KEY_HIGHEST_LEVEL = "HIGHEST_LEVEL"
        private const val KEY_SOUND_ENABLED = "SOUND_ENABLED"
        private const val PREFIX_STARS = "STARS_"
        private const val PREFIX_STICKERS = "STICKER_"
        private const val PREFIX_HINTS_USED = "HINTS_USED_"
        private const val PREFIX_SAVED_WORDS = "SAVED_WORDS_"
        private const val PREFIX_SAVED_HINTS = "SAVED_HINTS_"
    }

    fun getHighestLevel(): Int = prefs.getInt(KEY_HIGHEST_LEVEL, 1)

    fun saveHighestLevel(level: Int) {
        val current = getHighestLevel()
        if (level > current) {
            prefs.edit().putInt(KEY_HIGHEST_LEVEL, level).apply()
        }
    }

    fun getStarsForLevel(levelId: Int): Int = prefs.getInt("$PREFIX_STARS$levelId", 0)

    fun saveStarsForLevel(levelId: Int, stars: Int) {
        val current = getStarsForLevel(levelId)
        if (stars > current) {
            prefs.edit().putInt("$PREFIX_STARS$levelId", stars).apply()
        }
    }

    fun getHintsUsedForLevel(levelId: Int): Int = prefs.getInt("$PREFIX_HINTS_USED$levelId", 0)

    fun saveHintsUsedForLevel(levelId: Int, hints: Int) {
        prefs.edit().putInt("$PREFIX_HINTS_USED$levelId", hints).apply()
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true)

    fun toggleSound(): Boolean {
        val newVal = !isSoundEnabled()
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, newVal).apply()
        return newVal
    }

    fun isStickerUnlocked(stickerId: Int): Boolean =
        prefs.getBoolean("$PREFIX_STICKERS$stickerId", false)

    fun unlockSticker(stickerId: Int) {
        prefs.edit().putBoolean("$PREFIX_STICKERS$stickerId", true).apply()
    }

    fun getUnlockedStickers(): List<Int> {
        return (1..5).filter { isStickerUnlocked(it) }
    }

    fun getTotalStars(): Int {
        return (1..15).sumOf { getStarsForLevel(it) }
    }

    fun saveLevelProgress(levelId: Int, foundWords: Set<String>, hintCount: Int) {
        val encoded = foundWords.joinToString(",")
        prefs.edit()
            .putString("$PREFIX_SAVED_WORDS$levelId", encoded)
            .putInt("$PREFIX_SAVED_HINTS$levelId", hintCount)
            .apply()
    }

    fun loadLevelProgress(levelId: Int): LevelProgress? {
        val encoded = prefs.getString("$PREFIX_SAVED_WORDS$levelId", null) ?: return null
        val words = if (encoded.isBlank()) emptySet()
        else encoded.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
        val hints = prefs.getInt("$PREFIX_SAVED_HINTS$levelId", 0)
        return LevelProgress(foundWords = words, hintCount = hints)
    }

    fun clearLevelProgress(levelId: Int) {
        prefs.edit()
            .remove("$PREFIX_SAVED_WORDS$levelId")
            .remove("$PREFIX_SAVED_HINTS$levelId")
            .apply()
    }
}
