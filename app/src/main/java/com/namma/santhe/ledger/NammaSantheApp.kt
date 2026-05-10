package com.namma.santhe.ledger

import android.app.Application
import com.namma.santhe.ledger.ui.LanguageManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NammaSantheApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Load saved language on app start
        LanguageManager.init(this)
    }
}