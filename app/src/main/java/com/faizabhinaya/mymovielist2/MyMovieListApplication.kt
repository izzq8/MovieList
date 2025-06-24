package com.faizabhinaya.mymovielist2

import android.app.Application
import android.content.Context
import com.faizabhinaya.mymovielist2.ui.theme.ThemeManager

class MyMovieListApplication : Application() {

    // Initialize ThemeManager as a lazy property
    val themeManager by lazy { ThemeManager.getInstance(applicationContext) }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        @Volatile
        private var instance: MyMovieListApplication? = null

        fun getInstance(): MyMovieListApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }

    init {
        instance = this
    }
}
