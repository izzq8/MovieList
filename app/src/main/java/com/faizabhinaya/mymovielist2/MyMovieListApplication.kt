package com.faizabhinaya.mymovielist2

import android.app.Application
import android.content.Context
import com.faizabhinaya.utils.LocaleHelper

class MyMovieListApplication : Application() {
    override fun attachBaseContext(base: Context) {
        val language = LocaleHelper.getLanguage(base)
        super.attachBaseContext(LocaleHelper.setLocale(base, language))
    }

    override fun onCreate() {
        super.onCreate()
        val language = LocaleHelper.getLanguage(this)
        LocaleHelper.setLocale(this, language)
    }
}
