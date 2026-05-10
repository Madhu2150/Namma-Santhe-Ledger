package com.namma.santhe.ledger.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object LanguageManager {

    private const val PREFS_NAME = "namma_santhe_prefs"
    private const val KEY_LANGUAGE = "app_language"

    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.name)
        currentLanguage = AppLanguage.valueOf(saved ?: AppLanguage.ENGLISH.name)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        currentLanguage = language
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    val strings: AppStrings
        get() = when (currentLanguage) {
            AppLanguage.ENGLISH -> EnglishStrings
            AppLanguage.KANNADA -> KannadaStrings
        }
}