package com.shyampanchal.shopifymemorymatch.prefs

import android.content.Context
import android.content.SharedPreferences

const val PREFS_NAME = "MemoryMatchPreferences"
const val MATCHES_KEY = "numMatches"
const val HIGH_SCORE_KEY = "highScore"

class SharedPrefsManager(context: Context) {

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, 0)
    }
    private val prefsEditor: SharedPreferences.Editor by lazy { sharedPrefs.edit() }

    fun setMatches(numMatches: Int) {
        prefsEditor.putInt(MATCHES_KEY, numMatches)
        prefsEditor.apply()
    }

    fun readMatches() = sharedPrefs.getInt(MATCHES_KEY, 2)

    fun setHighScore(highScore: Int) {
        prefsEditor.putInt(HIGH_SCORE_KEY, highScore)
        prefsEditor.apply()
    }

    fun readHighScore() = sharedPrefs.getInt(HIGH_SCORE_KEY, 0)
}